package models

import play.api.libs.json.Json

/**
  * Marco Ebert 23.06.16
  */
case class Tag(id: Int, tag: String)

object Tag {

  /**
    * Implicit JSON format.
    */
  implicit val Format = Json.format[Tag]

  /**
    * ID field name.
    */
  val ID = "id"

  /**
    * Tag field name.
    */
  val Tag = "tag"

}
