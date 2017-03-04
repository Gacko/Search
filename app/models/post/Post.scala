package models.post

import models.comment.Comment
import models.info.Info
import models.item.Item
import models.tag.Tag
import play.api.libs.json.Format
import play.api.libs.json.JsObject
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
  implicit val Format: Format[Post] = Json.format[Post]

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
  val FlatTags = "flat_tags"

  /**
    * Comments field name.
    */
  val Comments = "comments"

  /**
    * Flat comments field name.
    */
  val FlatComments = "flat_comments"

  /**
    * Type mapping.
    */
  val Mapping: JsObject = Json.obj(
    "_all" -> Json.obj("enabled" -> false),
    "properties" -> Json.obj(
      ID -> Json.obj("type" -> "integer"),
      Promoted -> Json.obj("type" -> "integer"),
      Created -> Json.obj("type" -> "long"),
      Image -> Json.obj(
        "type" -> "text",
        "analyzer" -> "path"
      ),
      Thumb -> Json.obj(
        "type" -> "text",
        "analyzer" -> "path"
      ),
      FullSize -> Json.obj(
        "type" -> "text",
        "analyzer" -> "path"
      ),
      Width -> Json.obj("type" -> "integer"),
      Height -> Json.obj("type" -> "integer"),
      Audio -> Json.obj("type" -> "boolean"),
      Source -> Json.obj(
        "type" -> "text",
        "analyzer" -> "path"
      ),
      Flags -> Json.obj("type" -> "byte"),
      User -> Json.obj("type" -> "keyword"),
      Tags -> Json.obj(
        "type" -> "nested",
        "properties" -> Json.obj(
          Tag.ID -> Json.obj("type" -> "integer"),
          Tag.Tag -> Json.obj(
            "type" -> "text",
            "copy_to" -> FlatTags
          )
        )
      ),
      FlatTags -> Json.obj("type" -> "text"),
      Comments -> Json.obj(
        "type" -> "nested",
        "properties" -> Json.obj(
          Comment.ID -> Json.obj("type" -> "integer"),
          Comment.Parent -> Json.obj("type" -> "integer"),
          Comment.Content -> Json.obj(
            "type" -> "text",
            "copy_to" -> FlatComments
          ),
          Comment.Created -> Json.obj("type" -> "long"),
          Comment.Name -> Json.obj("type" -> "keyword")
        )
      ),
      FlatComments -> Json.obj("type" -> "text")
    )
  )

  /**
    * Create post from item and info.
    *
    * @param item Item.
    * @param info Info.
    * @return Post.
    */
  def from(item: Item)(info: Info): Post = {
    Post(
      id = item.id,
      promoted = item.promoted,
      created = item.created,
      image = item.image,
      thumb = item.thumb,
      fullsize = item.fullsize,
      width = item.width,
      height = item.height,
      audio = item.audio,
      source = item.source,
      flags = item.flags,
      user = item.user,
      tags = info.tags,
      comments = info.comments
    )
  }

}
