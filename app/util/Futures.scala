package util

import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.ListenableActionFuture

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.language.implicitConversions

/**
  * Marco Ebert 18.05.16
  */
object Futures {

  /**
    * Implicitly converts a Java ListenableActionFuture into a Scala Future.
    *
    * @param actionFuture Java ListenableActionFuture
    * @tparam T Result type of the ListenableActionFuture
    * @return Scala Future
    */
  implicit def listenableActionFuture2Future[T](actionFuture: ListenableActionFuture[T]): Future[T] = {
    // Create promise of T.
    val promise = Promise[T]()

    // Add action listener.
    actionFuture addListener new ActionListener[T] {

      /**
        * Fulfill promise on success.
        *
        * @param response Response.
        */
      override def onResponse(response: T): Unit = promise success response

      /**
        * Fail promise on exception.
        *
        * @param exception Exception.
        */
      override def onFailure(exception: Exception): Unit = promise failure exception

    }

    // Return promise as Future.
    promise.future
  }

}
