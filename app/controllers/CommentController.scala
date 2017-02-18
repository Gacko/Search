package controllers

import javax.inject.Inject
import javax.inject.Singleton

import dao.comment.CommentDAO
import models.comment.Comment
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class CommentController @Inject()(dao: CommentDAO, components: ControllerComponents) extends AbstractController(components) {

  /**
    * Implicit execution context.
    */
  private implicit val ec = defaultExecutionContext

  /**
    * Indexes a comment for a post.
    *
    * @param post Post ID.
    * @return If a post has been found and the comment has been indexed.
    */
  def index(post: Int): Action[Comment] = Action.async(parse.json[Comment]) { request =>
    val comment = request.body
    dao.index(post, comment) map { indexed =>
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
  def delete(post: Int, id: Int): Action[AnyContent] = Action.async {
    dao.delete(post, id) map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
