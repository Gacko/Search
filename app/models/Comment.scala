package models

import play.api.libs.json.Json

/**
  * Marco Ebert 21.05.16
  */
case class Comment(override val id: Int, post: Int, text: String) extends Entity

object Comment {

  /**
    * Implicit JSON format.
    */
  implicit val Format = Json.format[Comment]

  /**
    * Type name.
    */
  val Type = "comment"

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
        "post" -> Json.obj(
          "type" -> "integer"
        ),
        "text" -> Json.obj(
          "type" -> "string"
        )
      )
    )
  )

}
