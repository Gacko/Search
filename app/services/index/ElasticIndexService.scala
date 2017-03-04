package services.index

import javax.inject.Inject
import javax.inject.Singleton

import dao.index.IndexDAO
import org.elasticsearch.client.Client
import play.api.Logger
import play.api.libs.json.Json
import util.Futures._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Success

/**
  * Marco Ebert 10.10.16
  */
@Singleton
final class ElasticIndexService @Inject()(client: Client, index: IndexDAO) extends IndexService {

  /**
    * Creates an index.
    *
    * @return Index name.
    */
  private def create(implicit ec: ExecutionContext): Future[String] = {
    // Create name.
    val name = index.name

    // Prepare request.
    val request = client.admin.indices prepareCreate name
    // Get settings.
    val settings = Json stringify index.settings
    // Set settings.
    request setSettings settings

    // Get mappings.
    val mappings = index.mappings mapValues Json.stringify
    // Iterate mappings.
    for ((name, mapping) <- mappings) {
      // Add mapping.
      request.addMapping(name, mapping)
    }

    // Execute request.
    val responseFuture = request.execute
    // Handle response.
    for (_ <- responseFuture) yield {
      Logger info s"ElasticIndexService::create: Created index '$name'."
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
    // Prepare request.
    val request = client.admin.indices prepareDelete name
    // Execute request.
    val responseFuture = request.execute
    // Handle response.
    for (response <- responseFuture) yield {
      Logger info s"ElasticIndexService::delete: Deleted index '$name'."
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
    val responseFuture = request.execute
    // Handle response.
    for (response <- responseFuture) yield {
      // Extract aliases.
      val pairs = for {
        cursor <- response.getAliases.asScala
        value <- cursor.value.asScala
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
    // Prepare request.
    val request = client.admin.indices.prepareAliases

    // Remove alias.
    remove foreach { remove => request.removeAlias(remove, alias) }
    // Add alias.
    request.addAlias(name, alias)

    // Execute request.
    val responseFuture = request.execute
    // Handle response.
    for (response <- responseFuture) yield {
      Logger info s"ElasticIndexService::setAlias: Set alias '$alias' to index '$name'."
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
    // Get aliases.
    aliases flatMap { aliases =>
      // Get read alias.
      val readAlias = index.read
      // Get read index name.
      val readIndexOption = aliases get readAlias

      // Get write alias.
      val writeAlias = index.write
      // Get write index name.
      val writeIndexOption = aliases get writeAlias

      writeIndexOption match {
        // Write index exists and is not equal to read index.
        case Some(writeIndex) if writeIndexOption != readIndexOption =>
          setAlias(writeIndex, readAlias, readIndexOption) andThen {
            // Successfully set alias.
            case Success(true) =>
              // Get backup alias.
              index.backup match {
                // Backup alias exists.
                case Some(backupAlias) =>
                  // Get backup index name.
                  val backupIndexOption = aliases get backupAlias
                  // Move backup alias to read index and remove backup index.
                  for {
                    readIndex <- readIndexOption if readIndexOption != backupIndexOption
                    acknowledged <- setAlias(readIndex, backupAlias, backupIndexOption) if acknowledged
                    backupIndex <- backupIndexOption
                  } this delete backupIndex
                // Backup alias does not exist.
                case None =>
                  // Remove read index.
                  readIndexOption foreach delete
              }
          }
        // Write index does not exist or is equal to read index.
        case _ =>
          // Create index and set write alias.
          for {
            newIndex <- create
            acknowledged <- setAlias(newIndex, writeAlias, writeIndexOption)
          } yield acknowledged
      }
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
    // Get aliases.
    aliases flatMap { aliases =>
      // Get read alias.
      val readAlias = index.read
      // Get read index name.
      val readIndexOption = aliases get readAlias

      // Get write alias.
      val writeAlias = index.write
      // Get write index name.
      val writeIndexOption = aliases get writeAlias

      readIndexOption match {
        // Read index exists and is not equal to write index.
        case Some(readIndex) if readIndexOption != writeIndexOption =>
          // Set write alias to read index.
          setAlias(readIndex, writeAlias, writeIndexOption) andThen {
            // Successfully set alias.
            case Success(true) =>
              // Remove write index.
              writeIndexOption foreach delete
          }
        // Read index does not exist or is equal to write index.
        case _ =>
          // Get backup index name.
          val backupIndexOption = index.backup flatMap aliases.get

          backupIndexOption match {
            // Backup index exists and is not equal to read index.
            case Some(backupIndex) if backupIndexOption != readIndexOption =>
              // Set read alias to backup index.
              setAlias(backupIndex, readAlias, readIndexOption)
            // Backup index does not exist or is equal to read index.
            case _ => Future successful false
          }
      }
    }
  }

}
