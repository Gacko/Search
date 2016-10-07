package controllers

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

import actors.CrawlerActor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller

import scala.concurrent.duration._

/**
  * Marco Ebert 29.06.16
  */
@Singleton
final class CrawlerController @Inject()(@Named(CrawlerActor.Name) crawler: ActorRef) extends Controller {

  /**
    * Actor ask timeout.
    */
  private implicit val timeout: Timeout = 5.seconds

  /**
    * Tells the crawler actor to start crawling.
    *
    * @return Actor status.
    */
  def start = Action {
    crawler ! CrawlerActor.Start
    Ok(Json.obj("started" -> true))
  }

  /**
    * Tells the crawler actor to stop crawling.
    *
    * @return Actor status.
    */
  def stop = Action {
    crawler ! CrawlerActor.Stop
    Ok(Json.obj("stopped" -> true))
  }

  /**
    * Asks the crawler actor for the crawling status.
    *
    * @return Actor status.
    */
  def status = Action.async {
    val question = crawler ? CrawlerActor.Status
    question.mapTo[Boolean] map { answer =>
      Ok(Json.obj("status" -> answer))
    }
  }

}
