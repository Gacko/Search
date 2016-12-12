import actors.Crawler
import actors.Fetcher
import actors.Indexer
import akka.routing.BalancingPool
import com.google.inject.AbstractModule
import dao.comment.CommentDAO
import dao.comment.ElasticCommentDAO
import dao.info.InfoDAO
import dao.info.RestInfoDAO
import dao.item.ItemDAO
import dao.item.RestItemDAO
import dao.post.ElasticPostDAO
import dao.post.PostDAO
import dao.tag.ElasticTagDAO
import dao.tag.TagDAO
import play.api.libs.concurrent.AkkaGuiceSupport
import services.ElasticIndexService
import services.IndexService

/**
  * Marco Ebert 24.09.16
  */
final class Module extends AbstractModule with AkkaGuiceSupport {

  /**
    * Configure bindings.
    */
  override def configure(): Unit = {
    // Bind PostDAO to ElasticPostDAO.
    bind(classOf[PostDAO]) to classOf[ElasticPostDAO]
    // Bind TagDAO to ElasticTagDAO.
    bind(classOf[TagDAO]) to classOf[ElasticTagDAO]
    // Bind CommentDAO to ElasticCommentDAO.
    bind(classOf[CommentDAO]) to classOf[ElasticCommentDAO]

    // Bind ItemDAO to RestItemDAO.
    bind(classOf[ItemDAO]) to classOf[RestItemDAO]
    // Bind InfoDAO to RestInfoDAO.
    bind(classOf[InfoDAO]) to classOf[RestInfoDAO]

    // Bind IndexService to ElasticIndexService.
    bind(classOf[IndexService]) to classOf[ElasticIndexService]

    // Bind Crawler.
    bindActor[Crawler](Crawler.Name)
    // Bind Fetcher.
    bindActor[Fetcher](Fetcher.Name, BalancingPool(4).props)
    // Bind Indexer.
    bindActor[Indexer](Indexer.Name)
  }

}
