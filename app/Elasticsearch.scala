import java.net.InetAddress
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

import com.google.inject.AbstractModule
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.NodeBuilder
import play.api.Configuration
import play.api.Environment
import play.api.inject.ApplicationLifecycle

import scala.collection.JavaConversions._
import scala.concurrent.Future

/**
  * Marco Ebert 18.05.2016
  */
@Singleton
sealed class StartStop @Inject()(lifecycle: ApplicationLifecycle, client: Client) {

  /**
    * Close Elasticsearch connection on application stop.
    */
  lifecycle addStopHook { () =>
    Future successful client.close
  }

}

final class Elasticsearch(environment: Environment, configuration: Configuration) extends AbstractModule {

  /**
    * Pairs key and value into a single entry settings object.
    *
    * @param key   Key.
    * @param value Value.
    * @return Settings.
    */
  private def settings(key: String, value: String): Settings = {
    val builder = Settings.builder
    builder.put(key, value)
    builder.build
  }

  /**
    * Starts a local cluster and creates a client connected to it.
    *
    * @return Client connected to local cluster.
    */
  private def local: Client = {
    val builder = NodeBuilder.nodeBuilder
    // Setup home path.
    builder settings settings("path.home", environment.rootPath.getAbsolutePath)
    // Setup node role.
    builder client false
    builder data true
    builder local true
    // Set cluster name if defined.
    configuration getString "cluster.name" match {
      case Some(cluster) => builder clusterName cluster
      case None =>
    }
    // Start node and return client.
    builder.node.client
  }

  /**
    * Creates a client connected to the configured Elasticsearch cluster.
    *
    * Configured by cluster.name and cluster.nodes.
    *
    * @return
    */
  private def transport: Client = {
    val builder = TransportClient.builder
    // Set cluster name if defined.
    configuration getString "cluster.name" match {
      case Some(cluster) => builder settings settings("cluster.name", cluster)
      case None =>
    }
    // Build client.
    val client = builder.build

    // Add transport addresses.
    for {
      nodes <- configuration getStringList "cluster.nodes"
      node <- nodes
    } {
      // Parse node string into URI.
      val uri = new URI(node)
      // Get host and port.
      val host = uri.getHost
      val port = uri.getPort match {
        case -1 => 9300
        case p => p
      }
      // Create transport address.
      val address = new InetSocketTransportAddress(InetAddress getByName host, port)
      // Add transport address.
      client addTransportAddress address
    }
    // Return client.
    client
  }

  /**
    * Creates a client depending on cluster.local.
    *
    * @return Client.
    */
  private def client: Client = {
    configuration getBoolean "cluster.local" match {
      case Some(true) => local
      case _ => transport
    }
  }

  /**
    * Connect to Elasticsearch and configure bindings.
    */
  override def configure(): Unit = {
    bind(classOf[Client]) toInstance client
    bind(classOf[StartStop]).asEagerSingleton()
  }

}
