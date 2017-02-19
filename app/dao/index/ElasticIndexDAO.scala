package dao.index

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
final class ElasticIndexDAO @Inject()(configuration: Configuration) extends IndexDAO {

  /**
    * Base name.
    */
  private val Base = "posts"

  /**
    * Shard count.
    */
  private val Shards = configuration get[Int] "index.shards"

  /**
    * Replica count.
    */
  private val Replicas = configuration get[Int] "index.replicas"

  /**
    * Backup enabled.
    */
  private val Backup: Boolean = configuration get[Boolean] "index.backup"

  /**
    * Unique name with timestamp.
    *
    * @return Unique name with timestamp.
    */
  override def name: String = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    val timestamp = sdf format new Date

    s"$Base-$timestamp"
  }

  /**
    * Search alias.
    *
    * @return Search alias.
    */
  override def read: String = s"$Base-read"

  /**
    * Index alias.
    *
    * @return Index alias.
    */
  override def write: String = s"$Base-write"

  /**
    * Backup alias.
    *
    * @return Backup alias.
    */
  override def backup: Option[String] = if (Backup) Some(s"$Base-backup") else None

  /**
    * Index settings.
    *
    * @return Index settings.
    */
  override def settings: String = Json.stringify(
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
    *
    * @return Index mappings.
    */
  override def mappings: Map[String, String] = Map(
    Post.Type -> Post.Mapping
  )

}
