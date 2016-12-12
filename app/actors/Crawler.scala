package actors

import javax.inject.Inject
import javax.inject.Named

import akka.actor.Actor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import dao.item.ItemDAO
import models.info.Info
import models.item.Items
import models.post.Post
import models.post.Posts
import play.api.Logger

import scala.concurrent.Future
import scala.concurrent.duration._
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

final class Crawler @Inject()(dao: ItemDAO, @Named(Fetcher.Name) fetcher: ActorRef, @Named(Indexer.Name) indexer: ActorRef) extends Actor {

  import context.dispatcher

  private implicit val Timeout: Timeout = 20.seconds

  /**
    * Busy behaviour.
    */
  private def busy: Receive = {
    case Crawler.Find(newer) =>
      // Find items.
      dao find Some(newer) onComplete {
        case Success(Items(_, _, _, items)) =>
          if (items.nonEmpty) {
            Future.traverse(items) { item =>
              (fetcher ? item).mapTo[Info] recover { case exception =>
                Logger error s"Crawler::busy::find: Failed to fetch info ${item.id}: $exception"
                // Recover with empty info. Better than no item.
                Info(Seq.empty, Seq.empty)
              } map Post.from(item)
            } foreach { posts =>
              // Index posts.
              indexer ! Posts(posts)

              // Get IDs.
              val head = items.head.id
              val last = items.last.id

              Logger info s"Crawler::busy::find: $head - $last"

              // Continue crawling.
              self ! Crawler.Find(last)
            }
          } else {
            Logger info "Crawler::busy::find: No more items."
            // Stop crawling.
            self ! Crawler.Find(0)
          }
        case Failure(exception) =>
          Logger error s"Crawler::busy::find: Failed to fetch items newer than $newer: $exception"
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
