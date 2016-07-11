package services

import javax.inject.{Inject, Singleton}

import models.{Index, Post}
import org.elasticsearch.client.Client
import org.elasticsearch.index.query.MatchQueryBuilder.Operator
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json.Json
import util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marco Ebert 11.07.16
  */
@Singleton
final class SearchService @Inject()(client: Client, index: Index) {

  /**
    * Search result size.
    */
  private val Size = 100

  /**
    * Searches posts by term in tags.
    *
    * @param term Search term.
    * @return Posts containing term in tags.
    */
  def search(term: String): Future[Seq[Post]] = {
    val request = client.prepareSearch(index.read)
    request.setTypes(Post.Type)
    request.setSize(Size)
    request.addSort(Post.Created, SortOrder.DESC)

    val query = QueryBuilders.matchQuery(Post.FlatTags, term)
    query.operator(Operator.AND)

    request.setQuery(query)

    val response = request.execute()
    response.map { response =>
      for (hit <- response.getHits.hits()) yield {
        Json.parse(hit.source()).validate[Post].get
      }
    }
  }

}
