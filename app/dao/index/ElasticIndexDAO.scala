package dao.index

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

import models.post.Post
import play.api.Configuration
import play.api.libs.json.JsObject
import play.api.libs.json.Json

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class ElasticIndexDAO @Inject()(configuration: Configuration) extends IndexDAO {

  /**
    * Timestamp date formatter.
    */
  private val SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")

  /**
    * Base name.
    */
  private val Base: String = configuration get[String] "index.name"

  /**
    * Shard count.
    */
  private val Shards: Int = configuration get[Int] "index.shards"

  /**
    * Replica count.
    */
  private val Replicas: Int = configuration get[Int] "index.replicas"

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
    val timestamp = SDF format new Date
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
  override def settings: JsObject = Json.obj(
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

  /**
    * Index mappings.
    *
    * @return Index mappings.
    */
  override def mappings: Map[String, JsObject] = Map(
    Post.Type -> Post.Mapping
  )

}
