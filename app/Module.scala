import com.google.inject.AbstractModule
import dao.comment.CommentDAO
import dao.comment.ElasticCommentDAO
import dao.post.ElasticPostDAO
import dao.post.PostDAO
import dao.tag.ElasticTagDAO
import dao.tag.TagDAO
import services.ElasticIndexService
import services.IndexService

/**
  * Marco Ebert 24.09.16
  */
final class Module extends AbstractModule {

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

    // Bind IndexService to ElasticIndexService.
    bind(classOf[IndexService]) to classOf[ElasticIndexService]
  }

}
