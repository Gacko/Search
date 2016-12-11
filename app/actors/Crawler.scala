package actors

import javax.inject.Inject

import akka.actor.Actor
import dao.item.ItemDAO
import models.item.Items
import play.api.Logger

import scala.util.Failure
import scala.util.Success

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

  /**
    * Find items.
    *
    * @param newer Items newer than this.
    */
  case class Find(newer: Int)

}

final class Crawler @Inject()(dao: ItemDAO) extends Actor {

  import context.dispatcher

  /**
    * Busy behaviour.
    */
  private def busy: Receive = {
    case Crawler.Find(newer) =>
      // Find items.
      dao find Some(newer) onComplete {
        case Success(Items(_, _, _, items)) =>
          if (items.nonEmpty) {
            // Get IDs.
            val head = items.head.id
            val last = items.last.id

            Logger info s"Crawler::busy::crawl: $head - $last"

            // Continue crawling.
            self ! Crawler.Find(last)
          } else {
            Logger info "Crawler::busy::crawl: No more items."
            // Stop crawling.
            self ! Crawler.Find(0)
          }
        case Failure(exception) =>
          Logger error s"Crawler::busy::crawl: Failed to fetch items newer than $newer: $exception"
          // Retry.
          self ! Crawler.Find(newer)
      }
    // Already crawling.
    case Crawler.Start =>
      Logger info "Crawler::busy::start: Already crawling."
      // Return failure.
      sender ! false
    // Stop crawling.
    case Crawler.Stop =>
      // Return to default behaviour.
      context.unbecome()
      Logger info "Crawler::busy::stop: Stopped crawling."
      // Return success.
      sender ! true
  }

  /**
    * Default behaviour.
    */
  override def receive: Receive = {
    // Start crawling.
    case Crawler.Start =>
      Logger info "Crawler::receive::start: Starting crawling."
      // Become busy.
      context become busy
      // Start crawling.
      self ! Crawler.Find(0)
      // Return success.
      sender ! true
    // Not crawling.
    case Crawler.Stop =>
      Logger info "Crawler::receive::stop: Not crawling."
      // Return failure.
      sender ! false
  }

}
