package dao.comment

import models.comment.Comment

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
trait CommentDAO {

  /**
    * Indexes a comment for a post.
    *
    * @param post    Post ID.
    * @param comment Comment.
    * @return If a post has been found and the comment has been indexed.
    */
  def index(post: Int, comment: Comment)(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Deletes a comment of a post.
    *
    * @param post Post ID.
    * @param id   Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  def delete(post: Int, id: Int)(implicit ec: ExecutionContext): Future[Boolean]

}
