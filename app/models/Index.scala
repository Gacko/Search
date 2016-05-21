package models

import play.api.libs.json.Json

/**
  * Marco Ebert 21.05.16
  */
object Index {

  /**
    * Index name.
    */
  val Name = "content"

  /**
    * Index settings.
    */
  val Settings: String = {
    Json.stringify(
      Json.obj(
        "number_of_replicas" -> 0,
        "number_of_shards" -> 4
      )
    )
  }

}
