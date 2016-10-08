package services.search

import models.post.Post

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 08.10.16
  */
trait SearchService {

  /**
    * Searches posts by term in tags.
    *
    * @param term Search term.
    * @return Posts containing term in tags.
    */
  def search(term: String)(implicit ec: ExecutionContext): Future[Seq[Post]]

}
