package services

import org.elasticsearch.client.Client
import util.Helpers._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Marco Ebert 20.05.16
  */
abstract class AbstractIndexService {

  protected def client: Client

  protected implicit def context: ExecutionContext

  protected def index: String

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
    * Deletes the index.
    *
    * @return
    */
  def delete: Future[Boolean] = {
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
