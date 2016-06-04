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
    * Switches indices.
    *
    * @return
    */
  def switch = Action.async {
    service.switch.map { switched =>
      Ok(Json.obj("switched" -> switched))
    }
  }

}
