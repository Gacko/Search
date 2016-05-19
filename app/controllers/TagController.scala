package controllers

import javax.inject.Inject

import models.{Tag, Tags}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Marco Ebert 19.05.16
  */
class TagController @Inject()(implicit context: ExecutionContext) extends Controller {

  /**
    * Indexes a tag.
    *
    * @return Index status.
    */
  def index = Action.async(parse.json[Tag]) { request =>
    val tag = request.body

    // Real indexing code will be asynchronous.
    val future = Future.successful {
      val status = Json.obj(
        "status" -> "indexed",
        "id" -> tag.id
      )
      status
    }

    future.map { status =>
      Ok(status)
    }
  }

  /**
    * Bulk indexes many tags.
    *
    * @return Index status.
    */
  def bulk = Action.async(parse.json[Tags]) { request =>
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
