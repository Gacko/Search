package dao.item

import models.item.Items

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 08.10.16
  */
trait ItemDAO {

  /**
    * Fetches items by age, flags and promotion status.
    *
    * @param newer    Only items newer than this item ID.
    * @param older    Only items older than this item ID.
    * @param flags    SFW / NSFW / NSFL
    * @param promoted Promoted or not.
    * @return Items for given parameters.
    */
  def find(
    newer: Option[Int] = None,
    older: Option[Int] = None,
    flags: Option[Byte] = None,
    promoted: Option[Boolean] = None
  )(implicit ec: ExecutionContext): Future[Items]

}
