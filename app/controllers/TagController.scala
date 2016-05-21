package controllers

import javax.inject.{Inject, Singleton}

import models.Tags
import play.api.libs.json.Json
import play.api.mvc.Action
import services.TagService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 19.05.16
  */
@Singleton
final class TagController @Inject()(override val service: TagService) extends AbstractController {

  /**
    * Indexes tags.
    *
    * @return Index status.
    */
  def index = Action.async(parse.json[Tags]) { request =>
    val tags = request.body.tags

    // Real indexing code will be asynchronous.
    val future = Future.successful {
      val status = Json.obj(
        "status" -> "indexed",
        "count" -> tags.length
      )
      status
    }

    future.map { status =>
      Ok(status)
    }
  }

  /**
    * Deletes a tag by ID.
    *
    * @param id Tag ID.
    * @return Deletion status.
    */
  def delete(id: String) = Action.async {
    // Real deletion code will be asynchronous.
    val future = Future.successful {
      val status = Json.obj(
        "status" -> "deleted",
        "id" -> id
      )
      status
    }

    future.map { status =>
      Ok(status)
    }
  }

}
