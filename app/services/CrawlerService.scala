package services

import javax.inject.Inject
import javax.inject.Singleton

import models.Info
import models.Item
import models.Items
import models.Post
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient

import scala.concurrent.Future

/**
  * Marco Ebert 02.07.16
  */
@Singleton
final class CrawlerService @Inject()(ws: WSClient, configuration: Configuration) {

  /**
    * Items URL.
    */
  private val Items = configuration getString "url.items" getOrElse "http://pr0gramm.com/api/items/get"

  /**
    * Info URL.
    */
  private val Info = configuration getString "url.info" getOrElse "http://pr0gramm.com/api/items/info"

  /**
    * Fetches items by age, flags and promotion status.
    *
    * @param newer    Only items newer than this item ID.
    * @param older    Only items older than this item ID.
    * @param flags    SFW / NSFW / NSFL
    * @param promoted Promoted or not.
    * @return Items for given parameters.
    */
  def items(newer: Option[Int], older: Option[Int], flags: Option[Byte], promoted: Option[Boolean]): Future[Items] = {
    // Prepare URL.
    val url = ws url Items
    // Build request with parameters if exist.
    val request = Seq(
      newer.map(n => "newer" -> n.toString),
      older.map(o => "older" -> o.toString),
      flags.map(f => "flags" -> f.toString),
      promoted.map(p => "promoted" -> (if (p) "1" else "0"))
    ).flatten match {
      case parameters if parameters.nonEmpty => url withQueryString (parameters: _*)
      case _ => url
    }

    // Execute request.
    request.get map { response =>
      // Extract items from response.
      response.json.validate[Items].get
    }
  }

  /**
    * Fetches an info by ID.
    *
    * @param id Item ID.
    * @return Info by item ID.
    */
  def info(id: Int): Future[Info] = {
    // Prepare URL and build request with parameter.
    val request = ws url Info withQueryString "itemId" -> id.toString

    // Execute request.
    request.get map { response =>
      // Extract info from response.
      response.json.validate[Info].get
    }
  }

  /**
    * Combines an item and its info into a post.
    *
    * @param item Item.
    * @param info Its info.
    * @return Post combined of item and info.
    */
  def post(item: Item, info: Info): Post = {
    Post(
      id = item.id,
      promoted = item.promoted,
      created = item.created,
      image = item.image,
      thumb = item.thumb,
      fullsize = item.fullsize,
      width = item.width,
      height = item.height,
      audio = item.audio,
      source = item.source,
      flags = item.flags,
      user = item.user,
      tags = info.tags,
      comments = info.comments
    )
  }

  /**
    * Fetches info for given items and combines them to posts.
    *
    * @param items Items to fetch info for.
    * @return Posts for items.
    */
  def posts(items: Seq[Item]): Future[Seq[Post]] = {
    items.foldLeft(Future.successful(Seq.empty[Post])) { (posts, item) =>
      for {
        posts <- posts
        info <- info(item.id)
      } yield {
        posts :+ post(item, info)
      }
    }
  }

}
