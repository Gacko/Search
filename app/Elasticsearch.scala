import java.net.InetAddress
import javax.inject.{Inject, Singleton}

import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
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
  lifecycle.addStopHook { () =>
    client.close()
    Future.successful(())
  }

}

class Elasticsearch extends AbstractModule {

  /**
    * Creates an instance of Client for Elasticsearch operations.
    *
    * Configured by cluster.name and cluster.nodes.
    *
    * @return
    */
  private def connect: Client = {
    val config = ConfigFactory.load()

    val clusterName = config.getString("cluster.name")
    val nodes = config.getStringList("cluster.nodes")

    val settings = Settings.builder().put("cluster.name", clusterName).build()
    val client = TransportClient.builder().settings(settings).build()

    for (node <- nodes) {
      val split = node.split(":", 2)

      val host = split(0)
      val port = split.length match {
        case 2 => split(1).toInt
        case 1 => 9300
      }

      val address = new InetSocketTransportAddress(InetAddress.getByName(host), port)
      client.addTransportAddress(address)
    }

    client
  }

  /**
    * Connect to Elasticsearch and configure bindings.
    */
  override def configure() = {
    val client = connect
    bind(classOf[Client]).toInstance(client)
    bind(classOf[StartStop]).asEagerSingleton()
  }

}
