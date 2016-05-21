package services

import javax.inject.{Inject, Singleton}

import models.Tag
import org.elasticsearch.client.Client
import play.api.libs.json.Json

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class TagService @Inject()(override val client: Client) extends AbstractService[Tag]("tags", Tag.Format) {

  override protected val settings: String = {
    Json.stringify(
      Json.obj(
        "number_of_replicas" -> 0,
        "number_of_shards" -> 4
      )
    )
  }

}
