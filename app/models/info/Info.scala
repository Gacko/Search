package models.info

import models.comment.Comment
import models.tag.Tag
import play.api.libs.json.Json
import play.api.libs.json.Reads

/**
  * Marco Ebert 29.06.16
  */
case class Info(tags: Seq[Tag], comments: Seq[Comment])

object Info {

  /**
    * Implicit JSON reader.
    */
  implicit val Reads: Reads[Info] = Json.reads[Info]

}
