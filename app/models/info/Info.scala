package models.info

import models.post.comment.Comment
import models.post.tag.Tag
import play.api.libs.json.Json

/**
  * Marco Ebert 29.06.16
  */
case class Info(tags: Seq[Tag], comments: Seq[Comment])

object Info {

  /**
    * Implicit JSON reader.
    */
  implicit val Reads = Json.reads[Info]

}
