package actors

import javax.inject.Inject

import akka.actor.Actor
import dao.info.InfoDAO
import models.item.Item

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

/**
  * Marco Ebert 12.12.16
  */
object Fetcher {

  /**
    * Actor name.
    */
  final val Name = "fetcher"

}

final class Fetcher @Inject()(dao: InfoDAO) extends Actor {

  import context.dispatcher

  /**
    * Info fetch timeout.
    */
  private val Timeout = 10.seconds

  /**
    * Indexes posts.
    */
  override def receive: Receive = {
    // Get info for item.
    case item: Item =>
      Try {
        Await.result(dao get item.id, Timeout)
      } match {
        case Success(info) => sender ! info
        case Failure(exception) => sender ! Failure(exception)
      }
  }

}
