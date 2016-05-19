package models

import play.api.libs.json.Json

/**
  * Marco Ebert 19.05.16
  */
case class Tag(id: Int, post: Int, text: String)

object Tag {

  implicit val Format = Json.format[Tag]

}
