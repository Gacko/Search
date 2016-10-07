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
    val promise = Promise[T]()

    actionFuture addListener new ActionListener[T] {

      override def onFailure(throwable: Throwable): Unit = promise failure throwable

      override def onResponse(response: T): Unit = promise success response

    }

    promise.future
  }

}
