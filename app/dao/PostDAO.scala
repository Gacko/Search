package dao

import models._

import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
trait PostDAO {

  /**
    * Indexes a post.
    *
    * @param post Post to index.
    * @return If the post has been indexed.
    */
  def index(post: Post): Future[Boolean]

  /**
    * Indexes multiple posts.
    *
    * @param posts Posts to index.
    * @return If the posts have been indexed.
    */
  def index(posts: Seq[Post]): Future[Boolean]

  /**
    * Deletes a post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int): Future[Boolean]

  /**
    * Indexes tags for a post.
    *
    * @param id   Post ID.
    * @param tags Tags.
    * @return If a post has been found and the tags have been indexed.
    */
  def indexTags(id: Int, tags: Seq[Tag]): Future[Boolean]

  /**
    * Deletes a tag of a post.
    *
    * @param id  Post ID.
    * @param tag Tag ID.
    * @return If a post has been found and the tag has been deleted.
    */
  def deleteTag(id: Int, tag: Int): Future[Boolean]

  /**
    * Indexes a comment for a post.
    *
    * @param id      Post ID.
    * @param comment Comment.
    * @return If a post has been found and the comment has been indexed.
    */
  def indexComment(id: Int, comment: Comment): Future[Boolean]

  /**
    * Deletes a comment of a post.
    *
    * @param id      Post ID.
    * @param comment Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  def deleteComment(id: Int, comment: Int): Future[Boolean]

}
