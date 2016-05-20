package services

import javax.inject.{Inject, Singleton}

import org.elasticsearch.client.Client
import play.api.libs.json.Json
import util.Helpers._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Marco Ebert 20.05.16
  */
@Singleton
class TagIndexService @Inject()(client: Client)(implicit context: ExecutionContext) {

  private val Index = "tags"

  private val Settings = Json.stringify(
    Json.obj(
      "number_of_replicas" -> 0,
      "number_of_shards" -> 4
    )
  )

  /**
    * Checks if the tag index exists.
    *
    * @return
    */
  def exists: Future[Boolean] = {
    val request = client.admin().indices().prepareExists(Index)
    val response = request.execute()
    response.map(_.isExists)
  }

  /**
    * Deletes the tag index.
    *
    * @return
    */
  def delete: Future[Boolean] = {
    val request = client.admin().indices().prepareDelete(Index)
    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Creates the tag index.
    *
    * @return
    */
  def create: Future[Boolean] = {
    val request = client.admin().indices().prepareCreate(Index)
    request.setSettings(Settings)
    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Rebuilds the tag index.
    *
    * @return
    */
  def rebuild: Future[Boolean] = {
    exists.flatMap {
      case true => delete
      case false => Future.successful(true)
    }.flatMap {
      case true => create
      case false => Future.successful(false)
    }
  }

}
