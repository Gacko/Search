package actors

import javax.inject.Inject

import actors.CrawlerActor.Start
import actors.CrawlerActor.Status
import actors.CrawlerActor.Stop
import akka.actor.Actor
import dao.PostDAO
import play.api.Logger
import services.CrawlerService

/**
  * Marco Ebert 24.09.16
  */
object CrawlerActor {

  /**
    * Actor name.
    */
  final val Name = "crawler-actor"

  /**
    * Start message.
    */
  case object Start

  /**
    * Stop message.
    */
  case object Stop

  /**
    * Status message.
    */
  case object Status

}

final class CrawlerActor @Inject()(crawler: CrawlerService, service: PostDAO) extends Actor {

  /**
    * Handle crawler actor messages.
    *
    * @return Receive.
    */
  override def receive: Receive = {
    case Start =>
      Logger.info("CrawlerActor::receive: Received start message.")
    case Stop =>
      Logger.info("CrawlerActor::receive: Received stop message.")
    case Status =>
      Logger.info("CrawlerActor::receive: Received status message.")
  }

}
