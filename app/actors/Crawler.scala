package actors

import akka.actor.Actor
import play.api.Logger

/**
  * Marco Ebert 11.12.16
  */
object Crawler {

  /**
    * Actor name.
    */
  final val Name = "crawler"

  /**
    * Start crawling.
    */
  case object Start

  /**
    * Stop crawling.
    */
  case object Stop

}

final class Crawler extends Actor {

  /**
    * Busy behaviour.
    */
  private def busy: Receive = {
    // Already crawling.
    case Crawler.Start =>
      Logger info "Crawler::busy::start: Already crawling."
      sender ! false
    // Stop crawling.
    case Crawler.Stop =>
      context.unbecome()
      Logger info "Crawler::busy::stop: Stopped crawling."
      sender ! true
  }

  /**
    * Default behaviour.
    */
  override def receive: Receive = {
    // Start crawling.
    case Crawler.Start =>
      Logger info "Crawler::receive::start: Starting crawling."
      context become busy
      sender ! true
    // Not crawling.
    case Crawler.Stop =>
      Logger info "Crawler::receive::stop: Not crawling."
      sender ! false
  }

}
