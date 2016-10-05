package controllers

import javax.inject.Inject
import javax.inject.Singleton

import dao.PostDAO
import play.api.mvc.Action
import play.api.mvc.Controller
import services.CrawlerService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Marco Ebert 29.06.16
  */
@Singleton
final class CrawlerController @Inject()(crawler: CrawlerService, postDAO: PostDAO) extends Controller {

  /**
    * Fetches items with infos and indexes them as posts.
    *
    * @param newer    Only items newer than this.
    * @param older    Only items older than this.
    * @param flags    SFW / NSFW / NSFL
    * @param promoted Promoted or not.
    * @return Last item ID.
    */
  def crawl(newer: Option[Int], older: Option[Int], flags: Option[Int], promoted: Option[Int]) = Action.async {
    val byteFlags = flags map { _.toByte }
    val promotedBoolean = promoted map { _ == 1 }
    val items = crawler.items(newer, older, byteFlags, promotedBoolean)

    items flatMap { items =>
      crawler posts items.items map { posts =>
        postDAO index posts

        val last = items.items.lastOption
        val id = last.fold(0)(_.id)

        Ok(id.toString)
      }
    }
  }

}
