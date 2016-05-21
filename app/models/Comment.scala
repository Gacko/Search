package models

import play.api.libs.json.Json

/**
  * Marco Ebert 21.05.16
  */
case class Comment(override val id: Int, post: Int, text: String) extends Entity

object Comment {

  implicit val Format = Json.format[Comment]

  /**
    * Type name used for Elasticsearch mapping.
    */
  val Type = "comment"

}
