package controllers

import javax.inject.Inject
import javax.inject.Singleton

import dao.tag.TagDAO
import models.tag.Tags
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class TagController @Inject()(dao: TagDAO, components: ControllerComponents) extends AbstractController(components) with DefaultExecutionContext {

  /**
    * Indexes tags for a post.
    *
    * @param post Post ID.
    * @return If a post has been found and the tags have been indexed.
    */
  def index(post: Int): Action[Tags] = Action.async(parse.json[Tags]) { request =>
    val tags = request.body.tags
    dao.index(post, tags) map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes a tag of a post.
    *
    * @param post Post ID.
    * @param id   Tag ID.
    * @return If a post has been found and the tag has been deleted.
    */
  def delete(post: Int, id: Int): Action[AnyContent] = Action.async {
    dao.delete(post, id) map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
