import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

import com.google.inject.AbstractModule
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import play.api.Configuration
import play.api.Environment
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * Marco Ebert 18.05.2016
  */
@Singleton
sealed class Shutdown @Inject()(lifecycle: ApplicationLifecycle, node: Node) {

  /**
    * Shutdown Elasticsearch node on application stop.
    */
  lifecycle addStopHook { () =>
    Future successful node.close
  }

}

@Singleton
sealed class Disconnect @Inject()(lifecycle: ApplicationLifecycle, client: Client) {

  /**
    * Close Elasticsearch connection on application stop.
    */
  lifecycle addStopHook { () =>
    Future successful client.close
  }

}

final class Elasticsearch(environment: Environment, configuration: Configuration) extends AbstractModule {

  /**
    * Whether to run a local cluster or connect to a remote one.
    */
  private val Local = configuration getBoolean "cluster.local" getOrElse false

  /**
    * Elasticsearch configuration file name.
    */
  private val YAML = "elasticsearch.yml"

  /**
    * Cluster name.
    */
  private val ClusterName = configuration getString "cluster.name"

  /**
    * Starts a local cluster and creates a client connected to it.
    *
    * @return Node and client connected to it.
    */
  private def local: (Node, Client) = {
    // Obtain a node builder.
    val builder = NodeBuilder.nodeBuilder

    // Create settings builder.
    val settings = Settings.builder
    // Attempt to load Elasticsearch configuration file.
    environment resourceAsStream YAML foreach { stream =>
      settings.loadFromStream(YAML, stream)
    }
    // Put home path.
    settings.put("path.home", environment.rootPath.getAbsolutePath)
    // Set settings.
    builder settings settings

    // Setup node role.
    builder client false
    builder data true
    builder local true

    // Set cluster name.
    ClusterName foreach builder.clusterName

    // Start node.
    val node = builder.node
    // Get client from node.
    val client = node.client
    // Return node and client.
    (node, client)
  }

  /**
    * Creates a client connected to the configured Elasticsearch cluster.
    *
    * @return Client connected to a remote cluster.
    */
  private def transport: Client = {
    // Obtain a client builder.
    val builder = TransportClient.builder

    // Set cluster name.
    ClusterName foreach { clusterName =>
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
    * Connect to Elasticsearch and configure bindings.
    */
  override def configure(): Unit = {
    // Obtain client.
    val client = if (Local) {
      // Start local node and obtain a client.
      val (node, client) = local
      // Bind Node to node instance.
      bind(classOf[Node]) toInstance node
      // Bind Shutdown as eager singleton.
      bind(classOf[Shutdown]).asEagerSingleton()
      // Return client.
      client
    } else transport

    // Bind Client to client instance.
    bind(classOf[Client]) toInstance client
    // Bind Disconnect as eager singleton.
    bind(classOf[Disconnect]).asEagerSingleton()
  }

}
