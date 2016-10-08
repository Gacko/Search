package models.post.comment

import play.api.libs.json.Json

/**
  * Marco Ebert 23.06.16
  */
case class Comment(id: Int, parent: Int, content: String, created: Long, name: String)

object Comment {

  /**
    * Implicit JSON format.
    */
  implicit val Format = Json.format[Comment]

  /**
    * ID field name.
    */
  val ID = "id"

  /**
    * Parent field name.
    */
  val Parent = "parent"

  /**
    * Content field name.
    */
  val Content = "content"

  /**
    * Created field name.
    */
  val Created = "created"

  /**
    * Name field name.
    */
  val Name = "name"

}
