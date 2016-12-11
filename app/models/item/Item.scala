package models.item

import play.api.libs.json.Json
import play.api.libs.json.Reads

/**
  * Marco Ebert 29.06.16
  */
case class Item(
  id: Int,
  promoted: Int,
  created: Long,
  image: String,
  thumb: String,
  fullsize: String,
  width: Int,
  height: Int,
  audio: Boolean,
  source: String,
  flags: Byte,
  user: String
)

object Item {

  /**
    * Implicit JSON reader.
    */
  implicit val Reads: Reads[Item] = Json.reads[Item]

}
