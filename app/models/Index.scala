package models

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}

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
  private val base = "posts"

  /**
    * Unique name with timestamp.
    *
    * @return Unique name with timestamp.
    */
  def name: String = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
    val timestamp = sdf.format(new Date())

    s"$base-$timestamp"
  }

  /**
    * Search alias.
    */
  val read = s"$base-read"

  /**
    * Index alias.
    */
  val write = s"$base-write"

  /**
    * Backup alias.
    */
  val backup = s"$base-backup"

  /**
    * Index settings.
    */
  val settings = Json.stringify(
    Json.obj(
      "index" -> Json.obj(
        "number_of_shards" -> configuration.getInt("index.shards"),
        "number_of_replicas" -> configuration.getInt("index.replicas")
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
