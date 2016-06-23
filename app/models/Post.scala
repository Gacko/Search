package models

import play.api.libs.json.Json

/**
  * Marco Ebert 06.06.16
  */
case class Post(
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
  user: String,
  tags: Seq[Tag],
  comments: Seq[Comment]
)

object Post {

  /**
    * Implicit JSON format.
    */
  implicit val Format = Json.format[Post]

  /**
    * Type name.
    */
  val Type = "post"

  /**
    * ID field name.
    */
  val ID = "id"

  /**
    * Promoted field name.
    */
  val Promoted = "promoted"

  /**
    * Created field name.
    */
  val Created = "created"

  /**
    * Image field name.
    */
  val Image = "image"

  /**
    * Thumb field name.
    */
  val Thumb = "thumb"

  /**
    * Full size field name.
    */
  val FullSize = "fullsize"

  /**
    * Width field name.
    */
  val Width = "width"

  /**
    * Height field name.
    */
  val Height = "height"

  /**
    * Audio field name.
    */
  val Audio = "audio"

  /**
    * Source field name.
    */
  val Source = "source"

  /**
    * Flags field name.
    */
  val Flags = "flags"

  /**
    * User field name.
    */
  val User = "user"

  /**
    * Tags field name.
    */
  val Tags = "tags"

  /**
    * Comments field name.
    */
  val Comments = "comments"

  /**
    * Type mapping.
    */
  val Mapping = Json.stringify(
    Json.obj(
      "_all" -> Json.obj(
        "enabled" -> false
      ),
      "properties" -> Json.obj(
        ID -> Json.obj(
          "type" -> "integer"
        ),
        Promoted -> Json.obj(
          "type" -> "integer"
        ),
        Created -> Json.obj(
          "type" -> "long"
        ),
        Image -> Json.obj(
          "type" -> "string"
        ),
        Thumb -> Json.obj(
          "type" -> "string"
        ),
        FullSize -> Json.obj(
          "type" -> "string"
        ),
        Width -> Json.obj(
          "type" -> "integer"
        ),
        Height -> Json.obj(
          "type" -> "integer"
        ),
        Audio -> Json.obj(
          "type" -> "boolean"
        ),
        Source -> Json.obj(
          "type" -> "string"
        ),
        Flags -> Json.obj(
          "type" -> "byte"
        ),
        User -> Json.obj(
          "type" -> "string"
        )
      )
    )
  )

}
