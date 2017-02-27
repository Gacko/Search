package controllers

import javax.inject.Inject
import javax.inject.Singleton

import dao.post.PostDAO
import models.post.Post
import models.post.Posts
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class PostController @Inject()(dao: PostDAO, components: ControllerComponents) extends AbstractController(components) {

  /**
    * Indexes a post.
    *
    * @return If the post has been indexed.
    */
  def index: Action[Post] = Action.async(parse.json[Post]) { request =>
    val post = request.body
    dao index post map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Indexes multiple posts.
    *
    * @return If the posts have been indexed.
    */
  def bulk: Action[Posts] = Action.async(parse.json[Posts]) { request =>
    val posts = request.body.posts
    dao index posts map { indexed =>
      Ok(Json.obj("indexed" -> indexed))
    }
  }

  /**
    * Finds posts by flags, promotion status, tags and/or user.
    *
    * @param flags    Flags.
    * @param promoted Promoted.
    * @param tags     Tags.
    * @param user     User.
    * @return Posts by flags, promotion status, tags and/or user.
    */
  def find(flags: Option[Int], promoted: Boolean, tags: Option[String], user: Option[String]): Action[AnyContent] = Action.async {
    // Find posts by tags, flags and promoted.
    dao.find(flags map (_.toByte), promoted, tags, user) map { posts =>
      // Wrap posts.
      val wrapper = Posts(posts)
      // Convert posts into JSON.
      val json = Json toJson wrapper
      // Return posts as JSON.
      Ok(json)
    }
  }

  /**
    * Deletes a post by ID.
    *
    * @param id Post ID.
    * @return If a post has been found and deleted.
    */
  def delete(id: Int): Action[AnyContent] = Action.async {
    dao delete id map { deleted =>
      Ok(Json.obj("deleted" -> deleted))
    }
  }

}
