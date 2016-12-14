package actors

import javax.inject.Inject
import javax.inject.Named

import akka.actor.Actor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import dao.item.ItemDAO
import models.info.Info
import models.item.Item
import models.item.Items
import models.post.Post
import models.post.Posts
import play.api.Configuration
import play.api.Logger

import scala.concurrent.ExecutionContext
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
  case class Crawl(newer: Int)

}

final class Crawler @Inject()(
  configuration: Configuration,
  dao: ItemDAO,
  @Named(Fetcher.Name) fetcher: ActorRef,
  @Named(Indexer.Name) indexer: ActorRef
) extends Actor {

  import context.dispatcher

  /**
    * Fetcher ask timeout.
    */
  private implicit val Timeout: Timeout = (configuration getMilliseconds s"${Crawler.Name}.timeout").fold(30.seconds)(_.milliseconds): FiniteDuration

  /**
    * Fetches posts for items.
    *
    * @param items Items.
    * @return Posts.
    */
  private def fetch(items: Seq[Item])(implicit ec: ExecutionContext): Future[Seq[Post]] = {
    Future.traverse(items) { item =>
      // Ask fetcher for info and recover in case of failure.
      (fetcher ? item).mapTo[Info] recover { case exception =>
        Logger error s"Crawler::fetch: Failed to fetch info ${item.id}: $exception"
        // Recover without info.
        Info(Seq.empty, Seq.empty)
      } map Post.from(item)
    }
  }

  /**
    * Busy behaviour.
    */
  private def busy: Receive = {
    // Crawl newer items.
    case Crawler.Crawl(newer) =>
      // Find items.
      dao find Some(newer) onComplete {
        case Success(Items(_, _, _, items)) if items.nonEmpty =>
          // Fetch posts.
          this fetch items andThen { case _ =>
            // Get IDs.
            val head = items.head.id
            val last = items.last.id

            Logger info s"Crawler::crawl: $head - $last"

            // Continue crawling.
            self ! Crawler.Crawl(last)
          } foreach { posts =>
            // Index posts.
            indexer ! Posts(posts)
          }
        case Success(_) =>
          Logger info "Crawler::crawl: No more items."
          // Continue from beginning.
          self ! Crawler.Crawl(0)
        case Failure(exception) =>
          Logger error s"Crawler::crawl: Failed to find items newer than $newer: $exception"
          // Retry.
          self ! Crawler.Crawl(newer)
      }
    // Already crawling.
    case Crawler.Start =>
      Logger info "Crawler::start: Already crawling."
      // Return failure.
      sender ! false
    // Stop crawling.
    case Crawler.Stop =>
      // Return to default behaviour.
      context.unbecome()
      Logger info "Crawler::stop: Stopped crawling."
      // Return success.
      sender ! true
  }

  /**
    * Default behaviour.
    */
  override def receive: Receive = {
    // Start crawling.
    case Crawler.Start =>
      Logger info "Crawler::start: Starting crawling."
      // Become busy.
      context become busy
      // Start crawling.
      self ! Crawler.Crawl(0)
      // Return success.
      sender ! true
    // Not crawling.
    case Crawler.Stop =>
      Logger info "Crawler::stop: Not crawling."
      // Return failure.
      sender ! false
  }

}
