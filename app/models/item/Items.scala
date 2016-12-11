package models.item

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads

/**
  * Marco Ebert 29.06.16
  */
case class Items(atStart: Boolean, atEnd: Boolean, error: Option[JsValue], items: Seq[Item])

object Items {

  /**
    * Implicit JSON reader.
    */
  implicit val Reads: Reads[Items] = Json.reads[Items]

}
