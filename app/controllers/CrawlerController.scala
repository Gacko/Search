package controllers

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

import actors.Crawler
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller

import scala.concurrent.duration._

/**
  * Marco Ebert 28.11.16
  */
@Singleton
final class CrawlerController @Inject()(@Named(Crawler.Name) crawler: ActorRef) extends Controller {

  /**
    * Ask timeout.
    */
  private implicit val Timeout: Timeout = 5.seconds

  /**
    * Asks crawler to start crawling.
    *
    * @return Status.
    */
  def start(from: Option[Int]): Action[AnyContent] = Action.async {
    // Ask crawler to start crawling.
    val question = crawler ? Crawler.Start(from)
    // Map answer.
    question.mapTo[Boolean] recover {
      // Recover from failure.
      case exception =>
        Logger error s"CrawlerController::start: Failed to ask crawler due to an exception: $exception"
        false
    } map { started =>
      // Parse answer as JSON.
      Ok(Json.obj("started" -> started))
    }
  }

  /**
    * Asks crawler to stop crawling.
    *
    * @return Status.
    */
  def stop: Action[AnyContent] = Action.async {
    // Ask crawler to stop crawling.
    val question = crawler ? Crawler.Stop
    // Map answer.
    question.mapTo[Boolean] recover {
      // Recover from failure.
      case exception =>
        Logger error s"CrawlerController::stop: Failed to ask crawler due to an exception: $exception"
        false
    } map { stopped =>
      // Parse answer as JSON.
      Ok(Json.obj("stopped" -> stopped))
    }
  }

}
