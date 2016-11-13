package dao.info

import models.info.Info

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 08.10.16
  */
trait InfoDAO {

  /**
    * Fetches an info by ID.
    *
    * @param id Item ID.
    * @return Info by item ID.
    */
  def get(id: Int)(implicit ec: ExecutionContext): Future[Option[Info]]

}
