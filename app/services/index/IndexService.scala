package services.index

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 21.05.16
  */
trait IndexService {

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
  def switch(implicit ec: ExecutionContext): Future[Boolean]

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
  def rollback(implicit ec: ExecutionContext): Future[Boolean]

}
