package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.IndexService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class IndexController @Inject()(service: IndexService) extends Controller {

  /**
    * Creates the index.
    *
    * @return If the creation has been acknowledged.
    */
  def create = Action.async {
    service.create.map { created =>
      Ok(Json.obj("created" -> created))
    }
  }

  /**
    * Deletes the index.
    *
    * @return If the deletion has been acknowledged.
    */
  def delete = Action.async {
    service.delete.map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

  /**
    * Checks if the index exists.
    *
    * @return If the index exists.
    */
  def exists = Action.async {
    service.exists.map { exists =>
      Ok(Json.obj("exists" -> exists))
    }
  }

}
