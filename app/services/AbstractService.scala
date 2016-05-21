package services

import org.elasticsearch.client.Client
import play.api.libs.json.{Format, Json}
import util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
abstract class AbstractService[E](index: String, `type`: String)(implicit val format: Format[E]) {

  protected def client: Client

  protected def settings: String

  /**
    * Indexes multiple entities.
    *
    * @param entities Entities to index.
    * @return If everything has been indexed successfully.
    */
  def index(entities: Seq[E]): Future[Boolean] = {
    val bulk = client.prepareBulk()
    for (entity <- entities) {
      val json = Json.toJson(entity)
      val source = Json.stringify(json)
      val request = client.prepareIndex(index, `type`, source)
      bulk.add(request)
    }
    val response = bulk.execute()
    response.map(!_.hasFailures)
  }

  /**
    * Deletes a single document by ID.
    *
    * @param id Document ID.
    * @return If a document has been found and deleted.
    */
  def delete(id: String): Future[Boolean] = {
    val request = client.prepareDelete(index, `type`, id)
    val response = request.execute()
    response.map(_.isFound)
  }

  /**
    * Creates the index.
    *
    * @return If the creation has been acknowledged.
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
    * @return If the deletion has been acknowledged.
    */
  def drop: Future[Boolean] = {
    val request = client.admin().indices().prepareDelete(index)
    val response = request.execute()
    response.map(_.isAcknowledged)
  }

  /**
    * Checks if the index exists.
    *
    * @return If the index exists.
    */
  def exists: Future[Boolean] = {
    val request = client.admin().indices().prepareExists(index)
    val response = request.execute()
    response.map(_.isExists)
  }

}
