package models.tag

import play.api.libs.json.Json

/**
  * Marco Ebert 23.06.16
  */
case class Tags(tags: Seq[Tag])

object Tags {

  /**
    * Implicit JSON reader.
    */
  implicit val Reads = Json.reads[Tags]

}
