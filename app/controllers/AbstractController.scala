package controllers

import play.api.libs.json.{Format, Json}
import play.api.mvc.{Action, Controller}
import services.AbstractService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Marco Ebert 20.05.16
  */
abstract class AbstractController[E](implicit val format: Format[E]) extends Controller {

  protected def service: AbstractService[E]

  /**
    * Indexes a sequence of entities.
    *
    * @return Index status.
    */
  def index = Action.async(parse.json[Seq[E]]) { request =>
    val entities = request.body
    service.index(entities).map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes a document by ID.
    *
    * @param id Document ID.
    * @return Deletion status.
    */
  def delete(id: String) = Action.async {
    service.delete(id).map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

  /**
    * Creates the index.
    *
    * @return
    */
  def create = Action.async {
    service.create.map { created =>
      Ok(Json.obj("created" -> created))
    }
  }

  /**
    * Drops the index.
    *
    * @return
    */
  def drop = Action.async {
    service.drop.map { dropped =>
      Ok(Json.obj("dropped" -> dropped))
    }
  }

  /**
    * Checks if the index exists.
    *
    * @return
    */
  def exists = Action.async {
    service.exists.map {
      case true => Ok
      case false => NotFound
    }
  }

}
