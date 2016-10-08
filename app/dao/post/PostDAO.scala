package dao.post

import models.post.Post

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
trait PostDAO {

  /**
    * Indexes a post.
    *
    * @param post Post to index.
    * @return If the post has been indexed.
    */
  def index(post: Post)(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Indexes multiple posts.
    *
    * @param posts Posts to index.
    * @return If the posts have been indexed.
    */
  def index(posts: Seq[Post])(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Updates a post by providing an existing one to a function returning an updated one.
    *
    * @param id Post ID.
    * @param f  Function updating the given post.
    * @return If a post has been found and updated.
    */
  def update(id: Int)(f: Post => Post)(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Deletes a post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int)(implicit ec: ExecutionContext): Future[Boolean]

}
