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
    * Indexes multiple entities.
    *
    * @return If everything has been indexed successfully.
    */
  def index = Action.async(parse.json[Seq[E]]) { request =>
    service.index(request.body).map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes an entity by ID.
    *
    * @param id Entity ID.
    * @return If an entity has been found and deleted.
    */
  def delete(id: String) = Action.async {
    service.delete(id).map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
