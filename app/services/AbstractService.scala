package services

import org.elasticsearch.client.Client
import play.api.libs.json.Format
import util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
abstract class AbstractService[E](index: String, implicit val format: Format[E]) {

  protected def client: Client

  protected def settings: String

  /**
    * Creates the index.
    *
    * @return
    */
  def create: Future[Boolean] = {
    val request = client.admin().indices().prepareCreate(index)
    request.setSettings(settings)
    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Drops the index.
    *
    * @return
    */
  def drop: Future[Boolean] = {
    val request = client.admin().indices().prepareDelete(index)
    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Checks if the index exists.
    *
    * @return
    */
  def exists: Future[Boolean] = {
    val request = client.admin().indices().prepareExists(index)
    val response = request.execute()
    response.map(_.isExists)
  }

}
