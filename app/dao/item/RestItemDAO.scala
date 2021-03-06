package dao.item

import javax.inject.Inject
import javax.inject.Singleton

import models.item.Items
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 08.10.16
  */
@Singleton
final class RestItemDAO @Inject()(configuration: Configuration, ws: WSClient) extends ItemDAO {

  /**
    * Items URL.
    */
  private val URL = configuration get[String] "item.url"

  /**
    * Fetches items by age, flags and promotion status.
    *
    * @param newer    Only items newer than this item ID.
    * @param older    Only items older than this item ID.
    * @param flags    SFW / NSFW / NSFL
    * @param promoted Promoted or not.
    * @return Items for given parameters.
    */
  override def find(newer: Option[Int], older: Option[Int], flags: Option[Byte], promoted: Option[Boolean])(implicit ec: ExecutionContext): Future[Items] = {
    // Prepare URL.
    val url = ws url URL

    // Build request with parameters if exist.
    val request = Seq(
      newer map { n => "newer" -> n.toString },
      older map { o => "older" -> o.toString },
      flags map { f => "flags" -> f.toString },
      promoted map { p => "promoted" -> (if (p) "1" else "0") }
    ).flatten match {
      case Nil => url
      case parameters => url withQueryStringParameters (parameters: _*)
    }

    // Execute request.
    val responseFuture = request.get
    // Handle response.
    for (response <- responseFuture) yield {
      // Extract items from response.
      response.json.validate[Items].get
    }
  }

}
