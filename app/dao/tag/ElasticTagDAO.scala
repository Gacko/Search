package dao.tag

import javax.inject.Inject
import javax.inject.Singleton

import dao.post.PostDAO
import models.tag.Tag

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class ElasticTagDAO @Inject()(postDAO: PostDAO) extends TagDAO {

  /**
    * Indexes tags for a post.
    *
    * @param post Post ID.
    * @param tags Tags.
    * @return If a post has been found and the tags have been indexed.
    */
  override def index(post: Int, tags: Seq[Tag])(implicit ec: ExecutionContext): Future[Boolean] = postDAO.update(post) { post =>
    post.copy(tags = post.tags ++ tags)
  }

  /**
    * Deletes a tag of a post.
    *
    * @param post Post ID.
    * @param id   Tag ID.
    * @return If a post has been found and the tag has been deleted.
    */
  override def delete(post: Int, id: Int)(implicit ec: ExecutionContext): Future[Boolean] = postDAO.update(post) { post =>
    post.copy(tags = post.tags filterNot { tag => tag.id == id })
  }

}
