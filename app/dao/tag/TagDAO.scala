package dao.tag

import models._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
trait TagDAO {

  /**
    * Indexes tags for a post.
    *
    * @param post Post ID.
    * @param tags Tags.
    * @return If a post has been found and the tags have been indexed.
    */
  def index(post: Int, tags: Seq[Tag])(implicit ec: ExecutionContext): Future[Boolean]

  /**
    * Deletes a tag of a post.
    *
    * @param post Post ID.
    * @param id   Tag ID.
    * @return If a post has been found and the tag has been deleted.
    */
  def delete(post: Int, id: Int)(implicit ec: ExecutionContext): Future[Boolean]

}
