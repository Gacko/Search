package models

import play.api.libs.json.Json

/**
  * Marco Ebert 19.05.16
  */
case class Tags(tags: Seq[Tag])

object Tags {

  implicit val Format = Json.format[Tags]

}
