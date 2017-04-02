package dao.post

import javax.inject.Inject
import javax.inject.Singleton

import dao.index.IndexDAO
import models.post.Post
import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.engine.VersionConflictEngineException
import org.elasticsearch.index.query.Operator
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortOrder
import play.api.Configuration
import play.api.Logger
import play.api.libs.json.Json
import util.Futures._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class ElasticPostDAO @Inject()(configuration: Configuration, client: Client, index: IndexDAO) extends PostDAO {

  /**
    * Maximum update retries.
    */
  private val Retries = configuration get[Int] "post.index.retries"

  /**
    * Search result size.
    */
  private val Size = configuration get[Int] "post.find.size"

  /**
    * Possible flags.
    */
  private val Flags = Seq(1, 2, 4, 8)

  /**
    * Retrieves a post by ID.
    *
    * @param id Post ID.
    * @return Some post with version if it exists.
    */
  private def get(id: Int)(implicit ec: ExecutionContext): Future[Option[(Post, Long)]] = {
    // Create request.
    val request = client.prepareGet(index.read, Post.Type, id.toString)
    // Execute request.
    val responseFuture = request.execute
    // Handle response.
    responseFuture map {
      // Post exists.
      case response if response.isExists =>
        // Parse JSON.
        val json = Json parse response.getSourceAsBytes
        // Validate post.
        val post = json.validate[Post].get
        // Get document version.
        val version = response.getVersion
        // Return post and version as tuple.
        Some((post, version))
      case _ => None
    }
  }

  /**
    * Builds an index request for a post.
    *
    * @param post Post to index.
    * @return Index request for the post.
    */
  private def indexRequest(post: Post): IndexRequestBuilder = {
    // Get post ID as string.
    val id = post.id.toString
    // Convert post into JSON.
    val json = Json toJson post
    // Get JSON as string.
    val source = Json toBytes json
    // Build request, set source and return it.
    client.prepareIndex(index.write, Post.Type, id).setSource(source, XContentType.JSON)
  }

  /**
    * Indexes a post.
    *
    * @param post Post to index.
    * @return If the post has been indexed.
    */
  override def index(post: Post)(implicit ec: ExecutionContext): Future[Boolean] = {
    // Build request.
    val request = indexRequest(post)
    // Execute request.
    val responseFuture = request.execute
    // Handle response.
    for (response <- responseFuture) yield response.getId == post.id.toString
  }

  /**
    * Indexes a post with version.
    *
    * @param post    Post to index.
    * @param version Document version.
    * @return If the post has been indexed.
    */
  @throws[VersionConflictEngineException]
  private def index(post: Post, version: Long)(implicit ec: ExecutionContext): Future[Boolean] = {
    // Build request.
    val request = indexRequest(post)
    // Set version.
    request setVersion version
    // Execute request.
    val responseFuture = request.execute
    // Handle response.
    for (response <- responseFuture) yield response.getId == post.id.toString
  }

  /**
    * Indexes multiple posts.
    *
    * @param posts Posts to index.
    * @return If the posts have been indexed.
    */
  override def index(posts: Seq[Post])(implicit ec: ExecutionContext): Future[Boolean] = {
    // Create request.
    val bulk = client.prepareBulk
    // Add a index request for every post.
    for (post <- posts) {
      // Build index request.
      val request = indexRequest(post)
      // Add index request to bulk request.
      bulk add request
    }
    // Execute request.
    val responseFuture = bulk.execute
    // Handle response.
    for (response <- responseFuture) yield !response.hasFailures
  }

  /**
    * Parses posts from a search response.
    *
    * @param response Search response.
    * @return Posts.
    */
  private def posts(response: SearchResponse): Seq[Post] = {
    // Parse hits into posts.
    for (hit <- response.getHits.hits) yield {
      // Get source from hit.
      val source = hit.source
      // Parse JSON from source.
      val json = Json parse source
      // Validate post.
      json.validate[Post].get
    }
  }

  /**
    * Finds posts by flags, promotion status, tags and/or user.
    *
    * @param flags    Flags.
    * @param promoted Promoted.
    * @param tags     Tags.
    * @param user     User.
    * @return Posts by flags, promotion status, tags and/or user.
    */
  override def find(flags: Option[Byte], promoted: Boolean, tags: Option[String], user: Option[String])(implicit ec: ExecutionContext): Future[Seq[Post]] = {
    // Prepare request.
    val request = client prepareSearch index.read
    // Set type.
    request setTypes Post.Type
    // Set size.
    request setSize Size
    // Add sort.
    request.addSort(Post.Created, SortOrder.DESC)

    // Create boolean query.
    val query = QueryBuilders.boolQuery

    flags match {
      case Some(mask) =>
        // Match flag mask.
        val bool = QueryBuilders.boolQuery
        // Iterate possible flags.
        for (flag <- Flags if (mask & flag) != 0) {
          bool should QueryBuilders.termQuery(Post.Flags, flag)
        }
        // Add flags as filter clause.
        query filter bool
      case _ =>
    }

    if (promoted) {
      // Promoted posts only.
      query mustNot QueryBuilders.termQuery(Post.Promoted, 0)
    }

    // Add tags match query as must clause.
    tags map (QueryBuilders.matchQuery(Post.FlatTags, _) operator Operator.AND) foreach query.must

    // Add user term query as filter clause.
    user map (QueryBuilders.termQuery(Post.User, _)) foreach query.filter

    // Set query to request.
    request setQuery query

    // Execute request.
    val response = request.execute
    // Handle response.
    response map posts
  }

  /**
    * Updates a post by providing an existing one to a function returning an updated one.
    *
    * @param id Post ID.
    * @param f  Function updating the given post.
    * @return If a post has been found and updated.
    */
  override def update(id: Int)(f: Post => Post)(implicit ec: ExecutionContext): Future[Boolean] = {
    /**
      * Internal update with retry on version conflict.
      *
      * @param retries Retries.
      * @return Updated.
      */
    def update(retries: Int): Future[Boolean] = {
      get(id) flatMap {
        // Found.
        case Some((post, version)) =>
          // Update post.
          val updated = f(post)
          // Index updated post.
          index(updated, version) recoverWith {
            // Version conflict, retries left.
            case _: VersionConflictEngineException if retries > 0 => update(retries - 1)
            // Version conflict, no retries left.
            case _: VersionConflictEngineException =>
              Logger error s"ElasticPostDAO::update: Failed to update post $id due to conflicting versions. No more retries left."
              Future successful false
          }
        // Not found.
        case None => Future successful false
      }
    }

    // Update with retries.
    update(Retries)
  }

  /**
    * Deletes a post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  override def delete(id: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    // Create request.
    val request = client.prepareDelete(index.write, Post.Type, id.toString)
    // Execute request.
    val responseFuture = request.execute
    // Handle response.
    for (response <- responseFuture) yield response.getResult == Result.DELETED
  }

}
