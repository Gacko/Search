import java.net.InetAddress
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

import com.google.inject.AbstractModule
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import play.api.Configuration
import play.api.Environment
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * Marco Ebert 04.03.17
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

final class Elasticsearch(environment: Environment, configuration: Configuration) extends AbstractModule {

  /**
    * Default Elasticsearch port.
    */
  private val DefaultPort = 9300

  /**
    * Creates a client connected to the configured Elasticsearch cluster.
    *
    * @return Client connected to the configured Elasticsearch cluster.
    */
  private def client: Client = {
    // Get cluster name.
    val cluster: String = configuration get[String] "cluster.name"
    // Build settings.
    val settings = Settings.builder.put("cluster.name", cluster).build
    // Build client.
    val client = new PreBuiltTransportClient(settings)

    // Get nodes.
    val nodes: Seq[String] = configuration get[Seq[String]] "cluster.nodes"
    // Add transport addresses.
    for (node <- nodes) {
      // Parse URI. Prepend fake scheme to make it valid.
      val uri = new URI(s"elasticsearch://$node")
      // Get host.
      val host = uri.getHost
      // Get port.
      val port = uri.getPort match {
        // No port specified, use default.
        case -1 => DefaultPort
        // Port specified, use it.
        case p => p
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
    * Configure bindings.
    */
  override def configure(): Unit = {
    // Bind Client to client instance.
    bind(classOf[Client]) toInstance client
    // Bind Disconnect as eager singleton.
    bind(classOf[Disconnect]).asEagerSingleton()
  }

}
