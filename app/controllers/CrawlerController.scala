package controllers

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

import actors.CrawlerActor
import akka.actor.ActorRef
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller

/**
  * Marco Ebert 29.06.16
  */
@Singleton
final class CrawlerController @Inject()(@Named(CrawlerActor.Name) crawler: ActorRef) extends Controller {

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

}
