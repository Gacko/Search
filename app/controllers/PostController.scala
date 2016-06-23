package controllers

import javax.inject.{Inject, Singleton}

import models.Post
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.PostService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class PostController @Inject()(service: PostService) extends Controller {

  /**
    * Indexes a post.
    *
    * @return If a post has been indexed successfully.
    */
  def index = Action.async(parse.json[Post]) { request =>
    service.index(request.body).map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes an post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int) = Action.async {
    service.delete(id).map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
