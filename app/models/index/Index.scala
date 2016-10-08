package models.index

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

import models.post.Post
import play.api.Configuration
import play.api.libs.json.Json

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class Index @Inject()(configuration: Configuration) {

  /**
    * Base name.
    */
  private val Base = "posts"

  /**
    * Shard count.
    */
  private val Shards = configuration getInt "index.shards"

  /**
    * Replica count.
    */
  private val Replicas = configuration getInt "index.replicas"

  /**
    * Unique name with timestamp.
    *
    * @return Unique name with timestamp.
    */
  def name: String = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    val timestamp = sdf format new Date

    s"$Base-$timestamp"
  }

  /**
    * Search alias.
    */
  val read = s"$Base-read"

  /**
    * Index alias.
    */
  val write = s"$Base-write"

  /**
    * Backup alias.
    */
  val backup = s"$Base-backup"

  /**
    * Index settings.
    */
  val settings = Json.stringify(
    Json.obj(
      "index" -> Json.obj(
        "number_of_shards" -> Shards,
        "number_of_replicas" -> Replicas
      ),
      "analysis" -> Json.obj(
        "analyzer" -> Json.obj(
          "path" -> Json.obj(
            "type" -> "custom",
            "tokenizer" -> "path_hierarchy",
            "filter" -> Json.arr("lowercase")
          )
        )
      )
    )
  )

  /**
    * Index mappings.
    */
  val mappings = Map(
    Post.Type -> Post.Mapping
  )

}
