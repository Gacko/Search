package controllers

import javax.inject.Inject
import javax.inject.Singleton

import dao.post.tag.TagDAO
import models.post.tag.Tags
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class TagController @Inject()(dao: TagDAO) extends Controller {

  /**
    * Indexes tags for a post.
    *
    * @param post Post ID.
    * @return If a post has been found and the tags have been indexed.
    */
  def index(post: Int) = Action.async(parse.json[Tags]) { request =>
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
  def delete(post: Int, id: Int) = Action.async {
    dao.delete(post, id) map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
