package models.tag

import play.api.libs.json.Json
import play.api.libs.json.Reads

/**
  * Marco Ebert 23.06.16
  */
case class Tags(tags: Seq[Tag])

object Tags {

  /**
    * Implicit JSON reader.
    */
  implicit val Reads: Reads[Tags] = Json.reads[Tags]

}
