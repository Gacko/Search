package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.AbstractIndexService

import scala.concurrent.ExecutionContext

/**
  * Marco Ebert 20.05.16
  */
abstract class AbstractIndexController extends Controller {

  def service: AbstractIndexService

  implicit def context: ExecutionContext

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
    * Deletes the index.
    *
    * @return
    */
  def delete = Action.async {
    service.delete.map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

  /**
    * Checks if the index exists.
    *
    * @return
    */
  def exists = Action.async {
    service.exists.map { exists =>
      Ok(Json.obj("exists" -> exists))
    }
  }

}
