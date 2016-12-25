package dao.post

import javax.inject.Inject
import javax.inject.Singleton

import models.index.Index
import models.post.Post
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.engine.VersionConflictEngineException
import org.elasticsearch.index.query.MatchQueryBuilder.Operator
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
final class ElasticPostDAO @Inject()(configuration: Configuration, client: Client, index: Index) extends PostDAO {

  /**
    * Maximum update retries.
    */
  private val Retries = configuration getInt "post.index.retries" getOrElse 0

  /**
    * Search result size.
    */
  private val Size = configuration getInt "post.find.size" getOrElse 120

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
    val response = request.execute
    // Handle response.
    response map {
      // Post exists.
      case r if r.isExists =>
        // Parse JSON.
        val json = Json parse r.getSourceAsBytes
        // Validate post.
        val post = json.validate[Post].get
        // Get document version.
        val version = r.getVersion
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
  private def request(post: Post): IndexRequestBuilder = {
    // Get post ID as string.
    val id = post.id.toString
    // Convert post into JSON.
    val json = Json toJson post
    // Get JSON as string.
    val source = Json stringify json
    // Build request, set source and return it.
    client.prepareIndex(index.write, Post.Type, id) setSource source
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
    val request = this request post
    // Set version.
    request setVersion version
    // Execute request.
    val response = request.execute
    // Handle response.
    response map { response => response.getId == post.id.toString }
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
    * Indexes a post.
    *
    * @param post Post to index.
    * @return If the post has been indexed.
    */
  override def index(post: Post)(implicit ec: ExecutionContext): Future[Boolean] = {
    // Build request.
    val request = this request post
    // Execute request.
    val response = request.execute
    // Handle response.
    response map { response => response.getId == post.id.toString }
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
      val request = this request post
      // Add index request to bulk request.
      bulk add request
    }
    // Execute request.
    val response = bulk.execute
    // Handle response.
    response map { response => !response.hasFailures }
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
    def update(id: Int, retries: Int)(f: Post => Post): Future[Boolean] = {
      this get id flatMap {
        case Some((post, version)) =>
          val updated = f(post)
          index(updated, version) recoverWith {
            case _: VersionConflictEngineException if retries > 0 => update(id, retries - 1)(f)
            case _: VersionConflictEngineException =>
              Logger error s"ElasticPostDAO::update: Failed to update post $id due to conflicting versions. No more retries left."
              Future successful false
            case e: Throwable => throw e
          }
        case None => Future successful false
      }
    }

    update(id, Retries)(f)
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
    val response = request.execute
    // Handle response.
    response map { response => response.isFound }
  }

}
