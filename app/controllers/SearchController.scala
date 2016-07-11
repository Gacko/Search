package controllers

import javax.inject.{Inject, Singleton}

import models.Posts
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.SearchService

import scala.concurrent.ExecutionContext.Implicits.global

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
    service.search(term).map { posts =>
      val wrapper = Posts(posts)
      val json = Json.toJson(wrapper)
      Ok(json)
    }
  }

}
