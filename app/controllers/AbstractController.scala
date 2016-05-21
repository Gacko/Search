package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.AbstractService

/**
  * Marco Ebert 20.05.16
  */
abstract class AbstractController extends Controller {

  protected def service: AbstractService

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
    service.exists.map { exists =>
      Ok(Json.obj("exists" -> exists))
    }
  }

}
