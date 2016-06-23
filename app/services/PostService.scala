package services

import javax.inject.{Inject, Singleton}

import models.{Index, Post}
import org.elasticsearch.client.Client
import play.api.libs.json.Json
import util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class PostService @Inject()(client: Client, index: Index) {

  /**
    * Indexes a post.
    *
    * @param post Post to index.
    * @return If a post has been indexed successfully.
    */
  def index(post: Post): Future[Boolean] = {
    val json = Json.toJson(post)
    val source = Json.stringify(json)

    val request = client.prepareIndex(index.write, Post.Type, post.id.toString)
    request.setSource(source)

    val response = request.execute()
    response.map(_.isCreated)
  }

  /**
    * Deletes an post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int): Future[Boolean] = {
    val request = client.prepareDelete(index.write, Post.Type, id.toString)
    val response = request.execute()
    response.map(_.isFound)
  }

}
