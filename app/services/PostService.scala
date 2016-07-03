package services

import javax.inject.{Inject, Singleton}

import models._
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import play.api.libs.json.Json
import util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class PostService @Inject()(client: Client, index: Index) {

  /**
    * Retrieves a post by ID.
    *
    * @param id Post ID.
    * @return Some post if it exists.
    */
  private def get(id: Int): Future[Option[Post]] = {
    val request = client.prepareGet(index.read, Post.Type, id.toString)
    val response = request.execute()
    response.map {
      case r if r.isExists => Some(Json.parse(r.getSourceAsBytes).validate[Post].get)
      case r => None
    }
  }

  /**
    * Prepares an index request for a post.
    *
    * @param post Post to index.
    * @return Index request for the post.
    */
  private def request(post: Post): IndexRequestBuilder = {
    val json = Json.toJson(post)
    val source = Json.stringify(json)

    val request = client.prepareIndex(index.write, Post.Type, post.id.toString)
    request.setSource(source)

    request
  }

  /**
    * Indexes a post.
    *
    * @param post Post to index.
    * @return If the post has been indexed.
    */
  def index(post: Post): Future[Boolean] = {
    val request = this.request(post)
    val response = request.execute()
    response.map(_.getId == post.id.toString)
  }

  /**
    * Indexes multiple posts.
    *
    * @param posts Posts to index.
    * @return If the posts have been indexed.
    */
  def index(posts: Seq[Post]): Future[Boolean] = {
    val bulk = client.prepareBulk()

    for (post <- posts) {
      val request = this.request(post)
      bulk.add(request)
    }

    val response = bulk.execute()
    response.map(!_.hasFailures)
  }

  /**
    * Deletes a post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int): Future[Boolean] = {
    val request = client.prepareDelete(index.write, Post.Type, id.toString)
    val response = request.execute()
    response.map(_.isFound)
  }

  /**
    * Updates a post by providing an existing one to a function returning an updated one.
    *
    * @param id Post ID.
    * @param f  Function updating the given post.
    * @return If a post has been found and updated.
    */
  private def update(id: Int)(f: Post => Post): Future[Boolean] = {
    get(id).flatMap {
      case Some(post) => index(f(post))
      case None => Future.successful(false)
    }
  }

  /**
    * Indexes tags for a post.
    *
    * @param id   Post ID.
    * @param tags Tags.
    * @return If a post has been found and the tags have been indexed.
    */
  def indexTags(id: Int, tags: Seq[Tag]): Future[Boolean] = update(id) { post =>
    post.copy(tags = post.tags ++ tags)
  }

  /**
    * Deletes a tag of a post.
    *
    * @param id  Post ID.
    * @param tag Tag ID.
    * @return If a post has been found and the tag has been deleted.
    */
  def deleteTag(id: Int, tag: Int): Future[Boolean] = update(id) { post =>
    post.copy(tags = post.tags.filterNot(_.id == tag))
  }

  /**
    * Indexes a comment for a post.
    *
    * @param id      Post ID.
    * @param comment Comment.
    * @return If a post has been found and the comment has been indexed.
    */
  def indexComment(id: Int, comment: Comment): Future[Boolean] = update(id) { post =>
    post.copy(comments = post.comments :+ comment)
  }

  /**
    * Deletes a comment of a post.
    *
    * @param id      Post ID.
    * @param comment Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  def deleteComment(id: Int, comment: Int): Future[Boolean] = update(id) { post =>
    post.copy(comments = post.comments.filterNot(_.id == comment))
  }

}
