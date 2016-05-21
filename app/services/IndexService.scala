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
final class IndexService @Inject()(client: Client, index: Index) {

  /**
    * Creates the index.
    *
    * @return If the creation has been acknowledged.
    */
  def create: Future[Boolean] = {
    val request = client.admin().indices().prepareCreate(index.name)
    request.setSettings(index.settings)

    for ((name, mapping) <- index.mappings) {
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
    val request = client.admin().indices().prepareDelete(index.name)
    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Checks if the index exists.
    *
    * @return If the index exists.
    */
  def exists: Future[Boolean] = {
    val request = client.admin().indices().prepareExists(index.name)
    val response = request.execute()
    response.map(_.isExists)
  }

}
