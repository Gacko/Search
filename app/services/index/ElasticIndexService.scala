package services.index

import javax.inject.Inject
import javax.inject.Singleton

import models.index.Index
import org.elasticsearch.client.Client
import play.api.Logger
import util.Futures._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 10.10.16
  */
@Singleton
final class ElasticIndexService @Inject()(client: Client, index: Index) extends IndexService {

  /**
    * Creates an index.
    *
    * @return Index name.
    */
  private def create(implicit ec: ExecutionContext): Future[String] = {
    // Create name.
    val name = index.name
    Logger info s"IndexService::create: Creating index '$name'."

    // Prepare request.
    val request = client.admin.indices prepareCreate name
    // Set settings.
    request setSettings index.settings
    // Add mappings.
    for ((name, mapping) <- index.mappings) {
      request.addMapping(name, mapping)
    }

    // Execute request.
    val response = request.execute
    // Handle response.
    response map { response =>
      Logger info s"IndexService::create: Created index '$name'."
      // Return name.
      name
    }
  }

  /**
    * Deletes an index by name.
    *
    * @param name Index name.
    * @return Deletion status.
    */
  private def delete(name: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    Logger info s"IndexService::delete: Deleting index '$name'."
    // Prepare request.
    val request = client.admin.indices prepareDelete name
    // Execute request.
    val response = request.execute
    // Handle response.
    response map { response =>
      Logger info s"IndexService::delete: Deleted index '$name'."
      response.isAcknowledged
    }
  }

  /**
    * Maps index names by alias.
    *
    * @return Index names by alias.
    */
  private def aliases(implicit ec: ExecutionContext): Future[Map[String, String]] = {
    // Prepare request.
    val request = client.admin.indices.prepareGetAliases()
    // Execute request.
    val response = request.execute
    // Handle response.
    response map { response =>
      // Extract aliases.
      val pairs = for {
        cursor <- response.getAliases
        value <- cursor.value
      } yield {
        value.alias -> cursor.key
      }
      // Return as map.
      pairs.toMap
    }
  }

  /**
    * Adds an alias to an index. Optionally removes the alias from another index.
    *
    * @param name   Index to set alias to.
    * @param alias  Alias to set for index.
    * @param remove Index to remove alias from.
    * @return Alias status.
    */
  private def setAlias(name: String, alias: String, remove: Option[String])(implicit ec: ExecutionContext): Future[Boolean] = {
    Logger info s"IndexService::setAlias: Adding alias '$alias' to index '$name'."
    // Prepare request.
    val request = client.admin.indices.prepareAliases

    // Remove alias.
    remove foreach { remove => request.removeAlias(remove, alias) }
    // Add alias.
    request.addAlias(name, alias)

    // Execute request.
    val response = request.execute
    // Handle response.
    response map { response =>
      Logger info s"IndexService::setAlias: Added alias '$alias' to index '$name'."
      response.isAcknowledged
    }
  }

  /**
    * Sets the read alias to the write aliased index if they are not equal.
    * Sets the backup alias to the previously read aliased index.
    * Deletes the previously backup aliased index afterwards.
    *
    * OR
    *
    * Creates a write aliased index if none exists.
    *
    * @return
    */
  override def switch(implicit ec: ExecutionContext): Future[Boolean] = {
    Logger info s"IndexService::switch: Switching indices."
    // Get aliases.
    aliases flatMap { aliases =>
      // Get read index name.
      val read = aliases get index.read
      // Get write index name.
      val write = aliases.get(index.write)

      val switch = write match {
        // Write index exists but is not equal to read index.
        case Some(w) if read != write =>
          // Get backup index name.
          val backup = aliases get index.backup
          // Move backup alias to read index and remove if exists.
          for {
            r <- read if read != backup
            alias <- setAlias(r, index.backup, backup)
            b <- backup
          } delete(b)
          // Set read alias to write index.
          setAlias(w, index.read, read)
        case _ =>
          // Create index and set write alias.
          for {
            name <- create
            alias <- setAlias(name, index.write, write)
          } yield alias
      }

      switch foreach { _ => Logger info s"IndexService::switch: Switched indices." }
      switch
    }
  }

  /**
    * Sets the write alias to the read aliased index if they are not equal.
    * Deletes the previously write aliased index afterwards.
    *
    * OR
    *
    * Sets the read alias to the backup aliased index if read and write are equal.
    *
    * @return
    */
  override def rollback(implicit ec: ExecutionContext): Future[Boolean] = {
    Logger info s"IndexService::rollback: Rolling back indices."
    // Get aliases.
    aliases flatMap { aliases =>
      // Get read index name.
      val read = aliases get index.read
      // Get write index name.
      val write = aliases get index.write

      val rollback = read match {
        // Read index exists but is not equal to write index.
        case Some(r) if read != write =>
          // Set write alias to read index.
          val alias = setAlias(r, index.write, write)
          // Remove write index.
          alias foreach { _ => write foreach delete }
          alias
        case _ =>
          // Get backup index name.
          val backup = aliases get index.backup
          backup match {
            // Backup index exists but is not equal to read index.
            case Some(b) if read != backup =>
              // Set read alias to backup index.
              setAlias(b, index.read, read)
            case _ => Future.successful(false)
          }
      }

      rollback foreach { _ => Logger info s"IndexService::rollback: Rolled back indices." }
      rollback
    }
  }

}
