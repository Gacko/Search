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
    * Flat tags field name.
    */
  val FlatTags = "flatTags"

  /**
    * Comments field name.
    */
  val Comments = "comments"

  /**
    * Flat comments field name.
    */
  val FlatComments = "flatComments"

  /**
    * Type mapping.
    */
  val Mapping = Json.stringify(
    Json.obj(
      "_all" -> Json.obj(
        "enabled" -> false
      ),
      "properties" -> Json.obj(
        Image -> Json.obj(
          "type" -> "string",
          "analyzer" -> "path"
        ),
        Thumb -> Json.obj(
          "type" -> "string",
          "analyzer" -> "path"
        ),
        FullSize -> Json.obj(
          "type" -> "string",
          "analyzer" -> "path"
        ),
        Source -> Json.obj(
          "type" -> "string",
          "analyzer" -> "path"
        ),
        User -> Json.obj(
          "type" -> "string",
          "index" -> "not_analyzed"
        ),
        Tags -> Json.obj(
          "type" -> "nested",
          "properties" -> Json.obj(
            Tag.Tag -> Json.obj(
              "type" -> "string",
              "copy_to" -> FlatTags
            )
          )
        ),
        Comments -> Json.obj(
          "type" -> "nested",
          "properties" -> Json.obj(
            Comment.Content -> Json.obj(
              "type" -> "string",
              "copy_to" -> FlatComments
            ),
            Comment.Name -> Json.obj(
              "type" -> "string",
              "index" -> "not_analyzed"
            )
          )
        )
      )
    )
  )

}
