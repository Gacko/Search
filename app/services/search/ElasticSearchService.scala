package services.search

import javax.inject.Inject
import javax.inject.Singleton

import models.index.Index
import models.post.Post
import org.elasticsearch.client.Client
import org.elasticsearch.index.query.MatchQueryBuilder.Operator
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json.Json
import util.Futures._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Marco Ebert 11.07.16
  */
@Singleton
final class ElasticSearchService @Inject()(client: Client, index: Index) extends SearchService {

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
  override def search(term: String)(implicit ec: ExecutionContext): Future[Seq[Post]] = {
    // Prepare request.
    val request = client prepareSearch index.read
    // Set type.
    request setTypes Post.Type
    // Set size.
    request setSize Size
    // Add sort.
    request.addSort(Post.Created, SortOrder.DESC)

    // Create query.
    val query = QueryBuilders.matchQuery(Post.FlatTags, term)
    // Set operator.
    query operator Operator.AND

    // Set query to request.
    request setQuery query

    // Execute request.
    val response = request.execute
    // Handle response.
    response.map { response =>
      // Parse hits into posts.
      for (hit <- response.getHits.hits) yield {
        // Get source from hit.
        val source = hit.source
        // Parse JSON from source.
        val json = Json parse source
        // Validate post.
        json.validate[Post].get
      }
    }
  }

}
