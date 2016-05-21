package services

import models.{Entity, Index}
import org.elasticsearch.client.Client
import play.api.libs.json.{Format, Json}
import util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
abstract class AbstractService[E <: Entity](client: Client, index: Index, `type`: String)(implicit val format: Format[E]) {

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

      val request = client.prepareIndex(index.name, `type`, entity.id.toString)
      request.setSource(source)

      bulk.add(request)
    }

    val response = bulk.execute()
    response.map(!_.hasFailures)
  }

  /**
    * Deletes an entity by ID.
    *
    * @param id Entity ID.
    * @return If an entity has been found and deleted.
    */
  def delete(id: String): Future[Boolean] = {
    val request = client.prepareDelete(index.name, `type`, id)
    val response = request.execute()
    response.map(_.isFound)
  }

}
