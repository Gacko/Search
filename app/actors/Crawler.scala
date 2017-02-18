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
  case class Start(from: Option[Int])

  /**
    * Stop crawling.
    */
  case object Stop

  /**
    * Find items.
    *
    * @param newer Items newer than this.
    */
  private case class Crawl(newer: Int)

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
  private implicit val Timeout: Timeout = (configuration getMillis s"${Crawler.Name}.timeout").milliseconds: FiniteDuration

  /**
    * Fetches posts for items.
    *
    * @param items Items.
    * @return Posts.
    */
  private def fetch(items: Seq[Item])(implicit ec: ExecutionContext): Future[Seq[Post]] = {
    Future.traverse(items) { item =>
      // Ask fetcher for info and recover in case of failure.
      (fetcher ? item).mapTo[Info] recover { case _ =>
        Logger warn s"Crawler::fetch: Failed to fetch info ${item.id}."
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
      // Start measuring time.
      val start = System.currentTimeMillis
      // Find items.
      dao.find(newer = Some(newer), flags = Some(15)) onComplete {
        case Success(Items(_, _, _, items)) if items.nonEmpty =>
          // Fetch posts.
          this fetch items andThen { case _ =>
            // Stop measuring time.
            val took = System.currentTimeMillis - start
            // Calculate velocity in items per second.
            val velocity = items.size * 1000 / took

            // Get IDs.
            val head = items.head.id
            val last = items.last.id

            Logger info s"Crawler::crawl: $head - $last | $took ms | $velocity items/s"

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
        case Failure(_) =>
          Logger warn s"Crawler::crawl: Failed to find items newer than $newer."
          // Wait.
          Thread sleep 1000
          // Retry.
          self ! Crawler.Crawl(newer)
      }
    // Already crawling.
    case Crawler.Start(_) =>
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
    case Crawler.Start(from) =>
      Logger info "Crawler::start: Starting crawling."
      // Become busy.
      context become busy
      // Start crawling.
      self ! Crawler.Crawl(from getOrElse 0)
      // Return success.
      sender ! true
    // Not crawling.
    case Crawler.Stop =>
      Logger info "Crawler::stop: Not crawling."
      // Return failure.
      sender ! false
  }

}
