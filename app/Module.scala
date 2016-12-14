import actors.Crawler
import actors.Fetcher
import actors.Indexer
import akka.actor.Actor
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
import play.api.Configuration
import play.api.Environment
import play.api.libs.concurrent.AkkaGuiceSupport
import services.ElasticIndexService
import services.IndexService

import scala.reflect.ClassTag

/**
  * Marco Ebert 24.09.16
  */
final class Module(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {

  /**
    * Bind balancing pooled actor.
    *
    * @param name  Actor name.
    * @param clazz Class tag.
    * @tparam A Actor type.
    */
  private def bindPooledActor[A <: Actor](name: String)(implicit clazz: ClassTag[A]): Unit = {
    configuration getInt s"$name.pool.size" match {
      case Some(size) => bindActor[A](name, BalancingPool(size).props)
      case None => bindActor[A](name)
    }
  }

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
    bindPooledActor[Fetcher](Fetcher.Name)
    // Bind Indexer.
    bindActor[Indexer](Indexer.Name)
  }

}
