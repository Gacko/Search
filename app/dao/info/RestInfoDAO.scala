package dao.info

import javax.inject.Inject
import javax.inject.Singleton

import models.info.Info
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 08.10.16
  */
@Singleton
final class RestInfoDAO @Inject()(configuration: Configuration, ws: WSClient) extends InfoDAO {

  /**
    * Info URL.
    */
  private val URL = configuration getString "info.url" getOrElse "http://pr0gramm.com/api/items/info"

  /**
    * Fetches an info by ID.
    *
    * @param id Item ID.
    * @return Info by item ID.
    */
  override def get(id: Int)(implicit ec: ExecutionContext): Future[Option[Info]] = {
    // Prepare URL and build request with parameter.
    val request = ws url URL withQueryString "itemId" -> id.toString

    // Execute request.
    request.get map { response =>
      // Extract info from response.
      response.json.validate[Info].asOpt
    } recover {
      case _ => None
    }
  }

}
