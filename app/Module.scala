import com.google.inject.AbstractModule
import dao.info.InfoDAO
import dao.info.RestInfoDAO
import dao.item.ItemDAO
import dao.item.RestItemDAO
import dao.post.ElasticPostDAO
import dao.post.PostDAO
import dao.post.comment.CommentDAO
import dao.post.comment.ElasticCommentDAO
import dao.post.tag.ElasticTagDAO
import dao.post.tag.TagDAO
import play.api.libs.concurrent.AkkaGuiceSupport
import services.index.ElasticIndexService
import services.index.IndexService
import services.search.ElasticSearchService
import services.search.SearchService

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
    // Bind SearchService to ElasticSearchService.
    bind(classOf[SearchService]) to classOf[ElasticSearchService]
  }

}
