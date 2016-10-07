package dao.post

import javax.inject.Inject
import javax.inject.Singleton

import models._
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.index.engine.VersionConflictEngineException
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
final class ElasticPostDAO @Inject()(client: Client, index: Index, configuration: Configuration) extends PostDAO {

  /**
    * Maximum update retries.
    */
  private val Retries = configuration getInt "post.index.retries" getOrElse 0

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
        Some((post, version))
      case r => None
    }
  }

  /**
    * Builds an index request for a post.
    *
    * @param post Post to index.
    * @return Index request for the post.
    */
  private def request(post: Post): IndexRequestBuilder = {
    // Convert post into JSON.
    val json = Json toJson post
    // Get JSON as string.
    val source = Json stringify json
    // Get post ID as string.
    val id = post.id.toString
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
    val request = this.request(post)
    // Set version.
    request setVersion version
    // Execute request.
    val response = request.execute
    // Handle response.
    response map { response => response.getId == post.id.toString }
  }

  /**
    * Indexes a post.
    *
    * @param post Post to index.
    * @return If the post has been indexed.
    */
  override def index(post: Post)(implicit ec: ExecutionContext): Future[Boolean] = {
    // Build request.
    val request = this.request(post)
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
      val request = this.request(post)
      // Add index request to bulk request.
      bulk add request
    }
    // Execute request.
    val response = bulk.execute
    // Handle response.
    response map { response => response.hasFailures }
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

  /**
    * Updates a post by providing an existing one to a function returning an updated one.
    *
    * @param id Post ID.
    * @param f  Function updating the given post.
    * @return If a post has been found and updated.
    */
  private def update(id: Int, retries: Int = Retries)(f: Post => Post)(implicit ec: ExecutionContext): Future[Boolean] = {
    get(id) flatMap {
      case Some((post, version)) =>
        val updated = f(post)
        index(updated, version) recoverWith {
          case v: VersionConflictEngineException if retries > 0 => update(id, retries - 1)(f)
          case v: VersionConflictEngineException =>
            Logger.error(s"PostService::update: Failed to update post $id due to conflicting versions. No more retries left.")
            Future successful false
          case e: Throwable => throw e
        }
      case None => Future successful false
    }
  }

  /**
    * Indexes tags for a post.
    *
    * @param id   Post ID.
    * @param tags Tags.
    * @return If a post has been found and the tags have been indexed.
    */
  override def indexTags(id: Int, tags: Seq[Tag])(implicit ec: ExecutionContext): Future[Boolean] = update(id) { post =>
    post.copy(tags = post.tags ++ tags)
  }

  /**
    * Deletes a tag of a post.
    *
    * @param id  Post ID.
    * @param tag Tag ID.
    * @return If a post has been found and the tag has been deleted.
    */
  override def deleteTag(id: Int, tag: Int)(implicit ec: ExecutionContext): Future[Boolean] = update(id) { post =>
    post.copy(tags = post.tags filterNot { _.id == tag })
  }

  /**
    * Indexes a comment for a post.
    *
    * @param id      Post ID.
    * @param comment Comment.
    * @return If a post has been found and the comment has been indexed.
    */
  override def indexComment(id: Int, comment: Comment)(implicit ec: ExecutionContext): Future[Boolean] = update(id) { post =>
    post.copy(comments = post.comments :+ comment)
  }

  /**
    * Deletes a comment of a post.
    *
    * @param id      Post ID.
    * @param comment Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  override def deleteComment(id: Int, comment: Int)(implicit ec: ExecutionContext): Future[Boolean] = update(id) { post =>
    post.copy(comments = post.comments filterNot { _.id == comment })
  }

}
