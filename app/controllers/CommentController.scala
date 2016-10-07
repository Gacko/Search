package controllers

import javax.inject.Inject
import javax.inject.Singleton

import dao.comment.CommentDAO
import models.Comment
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class CommentController @Inject()(commentDAO: CommentDAO) extends Controller {

  /**
    * Indexes a comment for a post.
    *
    * @param post Post ID.
    * @return If a post has been found and the comment has been indexed.
    */
  def index(post: Int) = Action.async(parse.json[Comment]) { request =>
    val comment = request.body
    commentDAO.index(post, comment) map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes a comment of a post.
    *
    * @param post Post ID.
    * @param id   Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  def delete(post: Int, id: Int) = Action.async {
    commentDAO.delete(post, id) map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
