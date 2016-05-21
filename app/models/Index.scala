package models

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.libs.json.Json

/**
  * Marco Ebert 21.05.16
  */
@Singleton
class Index @Inject()(configuration: Configuration) {

  /**
    * Index name.
    */
  val name = "content"

  /**
    * Index settings.
    */
  val settings = Json.stringify(
    Json.obj(
      "index" -> Json.obj(
        "number_of_shards" -> configuration.getInt("index.shards"),
        "number_of_replicas" -> configuration.getInt("index.replicas")
      ),
      "analysis" -> Json.obj(
        "analyzer" -> Json.obj(
          "text" -> Json.obj(
            "type" -> "custom",
            "tokenizer" -> "whitespace",
            "filter" -> Json.arr("trim", "lowercase")
          )
        )
      )
    )
  )

  /**
    * Index mappings.
    */
  val mappings = Map(
    Tag.Type -> Tag.Mapping,
    Comment.Type -> Comment.Mapping
  )

}
