package models

import play.api.libs.json.Json

/**
  * Marco Ebert 06.06.16
  */
case class Post(override val id: Int, tags: Seq[String], comments: Seq[String]) extends Entity

object Post {

  /**
    * Implicit JSON format.
    */
  implicit val Format = Json.format[Post]

  /**
    * Type name.
    */
  val Type = "post"

  /**
    * Type mapping.
    */
  val Mapping = Json.stringify(
    Json.obj(
      "_all" -> Json.obj(
        "enabled" -> false
      ),
      "properties" -> Json.obj(
        "id" -> Json.obj(
          "type" -> "integer"
        ),
        "tags" -> Json.obj(
          "type" -> "string",
          "analyzer" -> "text"
        ),
        "comments" -> Json.obj(
          "type" -> "string",
          "analyzer" -> "text"
        )
      )
    )
  )

}
