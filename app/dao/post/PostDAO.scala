package dao.post

import models._

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
    * Deletes a post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int)(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Updates a post by providing an existing one to a function returning an updated one.
    *
    * @param id Post ID.
    * @param f  Function updating the given post.
    * @return If a post has been found and updated.
    */
  def update(id: Int)(f: Post => Post)(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Indexes a comment for a post.
    *
    * @param id      Post ID.
    * @param comment Comment.
    * @return If a post has been found and the comment has been indexed.
    */
  def indexComment(id: Int, comment: Comment)(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Deletes a comment of a post.
    *
    * @param id      Post ID.
    * @param comment Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  def deleteComment(id: Int, comment: Int)(implicit ec: ExecutionContext): Future[Boolean]

}
