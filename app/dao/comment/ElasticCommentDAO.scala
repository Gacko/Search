package dao.comment

import javax.inject.Inject
import javax.inject.Singleton

import dao.post.PostDAO
import models.comment.Comment

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class ElasticCommentDAO @Inject()(postDAO: PostDAO) extends CommentDAO {

  /**
    * Indexes a comment for a post.
    *
    * @param post    Post ID.
    * @param comment Comment.
    * @return If a post has been found and the comment has been indexed.
    */
  override def index(post: Int, comment: Comment)(implicit ec: ExecutionContext): Future[Boolean] = postDAO.update(post) { post =>
    post.copy(comments = post.comments :+ comment)
  }

  /**
    * Deletes a comment of a post.
    *
    * @param post Post ID.
    * @param id   Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  override def delete(post: Int, id: Int)(implicit ec: ExecutionContext): Future[Boolean] = postDAO.update(post) { post =>
    post.copy(comments = post.comments filterNot { comment => comment.id == id })
  }

}
