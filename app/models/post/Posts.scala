package models.post

import play.api.libs.json.Json

/**
  * Marco Ebert 28.06.16
  */
case class Posts(posts: Seq[Post])

object Posts {

  /**
    * Implicit JSON format.
    */
  implicit val Format = Json.format[Posts]

}
