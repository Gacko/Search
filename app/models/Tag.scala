package models

import play.api.libs.json.Json

/**
  * Marco Ebert 19.05.16
  */
case class Tag(override val id: Int, post: Int, text: String) extends Entity

object Tag {

  implicit val Format = Json.format[Tag]

  /**
    * Type name used for Elasticsearch mapping.
    */
  val Type = "tag"

}
