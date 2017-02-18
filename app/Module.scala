import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

import actors.Crawler
import actors.Fetcher
import actors.Indexer
import akka.actor.Actor
import akka.routing.BalancingPool
import com.google.inject.AbstractModule
import dao.comment.CommentDAO
import dao.comment.ElasticCommentDAO
import dao.index.ElasticIndexDAO
import dao.index.IndexDAO
import dao.info.InfoDAO
import dao.info.RestInfoDAO
import dao.item.ItemDAO
import dao.item.RestItemDAO
import dao.post.ElasticPostDAO
import dao.post.PostDAO
import dao.tag.ElasticTagDAO
import dao.tag.TagDAO
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import play.api.Configuration
import play.api.Environment
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.concurrent.AkkaGuiceSupport
import services.index.ElasticIndexService
import services.index.IndexService

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Marco Ebert 24.09.16
  */
@Singleton
sealed class Disconnect @Inject()(lifecycle: ApplicationLifecycle, client: Client) {

  /**
    * Close Elasticsearch connection on application stop.
    */
  lifecycle addStopHook { () =>
    Future successful client.close
  }

}

final class Module(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {

  /**
    * Creates a client connected to the configured Elasticsearch cluster.
    *
    * @return Client connected to the configured Elasticsearch cluster.
    */
  private def client: Client = {
    // Obtain a client builder.
    val builder = TransportClient.builder

    // Set cluster name.
    configuration getString "cluster.name" foreach { clusterName =>
      builder settings Settings.builder.put("cluster.name", clusterName)
    }

    // Build client.
    val client = builder.build

    // Add transport addresses.
    for {
      nodes <- configuration getStringSeq "cluster.nodes"
      node <- nodes
    } {
      // Parse node string.
      val split = node.split(":", 2)
      // Get host and port.
      val host = split(0)
      val port = split.length match {
        case 1 => 9300
        case 2 => split(1).toInt
      }

      // Create transport address.
      val address = new InetSocketTransportAddress(InetAddress getByName host, port)
      // Add transport address.
      client addTransportAddress address

      Logger info s"Elasticsearch::transport: Connected to $address."
    }

    // Return client.
    client
  }

  /**
    * Bind pooled actor.
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
    // Bind Client to client instance.
    bind(classOf[Client]) toInstance client
    // Bind Disconnect as eager singleton.
    bind(classOf[Disconnect]).asEagerSingleton()

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

    // Bind IndexDAO to ElasticIndexDAO.
    bind(classOf[IndexDAO]) to classOf[ElasticIndexDAO]
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
