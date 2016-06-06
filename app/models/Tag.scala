package models

import play.api.libs.json.Json

/**
  * Marco Ebert 19.05.16
  */
case class Tag(override val id: Int, post: Int, text: String) extends Entity

object Tag {

  /**
    * Implicit JSON format.
    */
  implicit val Format = Json.format[Tag]

  /**
    * Type name.
    */
  val Type = "tag"

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
          "type" -> "string",
          "analyzer" -> "text"
        )
      )
    )
  )

}
