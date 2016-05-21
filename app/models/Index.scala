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
  val Settings = Json.stringify(
    Json.obj(
      "settings" -> Json.obj(
        "number_of_replicas" -> 0,
        "number_of_shards" -> 4
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
  val Mappings = Map(
    Tag.Type -> Tag.Mapping,
    Comment.Type -> Comment.Mapping
  )

}
