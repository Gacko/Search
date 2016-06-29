package controllers

import javax.inject.{Inject, Singleton}

import models.{Comment, Post, Posts, Tags}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.PostService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class PostController @Inject()(service: PostService) extends Controller {

  /**
    * Indexes a post.
    *
    * @return If the post has been indexed.
    */
  def index = Action.async(parse.json[Post]) { request =>
    service.index(request.body).map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Indexes multiple posts.
    *
    * @return If the posts have been indexed.
    */
  def bulk = Action.async(parse.json[Posts]) { request =>
    service.bulk(request.body.posts).map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes a post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int) = Action.async {
    service.delete(id).map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

  /**
    * Indexes tags for a post.
    *
    * @param id Post ID.
    * @return If a post has been found and the tags have been indexed.
    */
  def indexTags(id: Int) = Action.async(parse.json[Tags]) { request =>
    service.indexTags(id, request.body.tags).map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes a tag of a post.
    *
    * @param id  Post ID.
    * @param tag Tag ID.
    * @return If a post has been found and the tag has been deleted.
    */
  def deleteTag(id: Int, tag: Int) = Action.async {
    service.deleteTag(id, tag).map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

  /**
    * Indexes a comment for a post.
    *
    * @param id Post ID.
    * @return If a post has been found and the comment has been indexed.
    */
  def indexComment(id: Int) = Action.async(parse.json[Comment]) { request =>
    service.indexComment(id, request.body).map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Deletes a comment of a post.
    *
    * @param id      Post ID.
    * @param comment Comment ID.
    * @return If a post has been found and the comment has been deleted.
    */
  def deleteComment(id: Int, comment: Int) = Action.async {
    service.deleteComment(id, comment).map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
