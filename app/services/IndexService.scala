package services

import javax.inject.{Inject, Singleton}

import models.Index
import org.elasticsearch.client.Client
import util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class IndexService @Inject()(client: Client) {

  /**
    * Creates the index.
    *
    * @return If the creation has been acknowledged.
    */
  def create: Future[Boolean] = {
    val request = client.admin().indices().prepareCreate(Index.Name)
    request.setSettings(Index.Settings)

    for ((name, mapping) <- Index.Mappings) {
      request.addMapping(name, mapping)
    }

    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Deletes the index.
    *
    * @return If the deletion has been acknowledged.
    */
  def delete: Future[Boolean] = {
    val request = client.admin().indices().prepareDelete(Index.Name)
    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Checks if the index exists.
    *
    * @return If the index exists.
    */
  def exists: Future[Boolean] = {
    val request = client.admin().indices().prepareExists(Index.Name)
    val response = request.execute()
    response.map(_.isExists)
  }

}
