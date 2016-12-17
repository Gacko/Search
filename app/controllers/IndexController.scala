package controllers

import javax.inject.Inject
import javax.inject.Singleton

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import services.IndexService

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
  def switch: Action[AnyContent] = Action.async {
    service.switch recover { case exception =>
      Logger error s"IndexController::switch: Failed to switch indices: $exception"
      false
    } map { switched =>
      Ok(Json.obj("switched" -> switched))
    }
  }

  /**
    * Rolls back indices.
    *
    * @return
    */
  def rollback: Action[AnyContent] = Action.async {
    service.rollback recover { case exception =>
      Logger error s"IndexController::rollback: Failed to rollback indices: $exception"
      false
    } map { rolledBack =>
      Ok(Json.obj("rolledBack" -> rolledBack))
    }
  }

}
