package controllers

import javax.inject.Inject
import javax.inject.Singleton

import models.Posts
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import services.SearchService

/**
  * Marco Ebert 11.07.16
  */
@Singleton
final class SearchController @Inject()(service: SearchService) extends Controller {

  /**
    * Searches posts by term in tags.
    *
    * @param term Search term.
    * @return Posts containing term in tags.
    */
  def search(term: String) = Action.async {
    // Search for term.
    service search term map { posts =>
      // Wrap posts.
      val wrapper = Posts(posts)
      // Convert posts into JSON.
      val json = Json toJson wrapper
      // Return posts as JSON.
      Ok(json)
    }
  }

}
