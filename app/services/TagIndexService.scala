package services

import javax.inject.Inject

import org.elasticsearch.client.Client
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

/**
  * Marco Ebert 20.05.16
  */
final class TagIndexService @Inject()(override val client: Client, override val context: ExecutionContext)() extends AbstractIndexService {

  override protected val index: String = "tags"

  override protected val settings: String = {
    Json.stringify(
      Json.obj(
        "number_of_replicas" -> 0,
        "number_of_shards" -> 4
      )
    )
  }

}
