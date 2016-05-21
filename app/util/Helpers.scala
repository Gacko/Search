package util

import org.elasticsearch.action.{ActionListener, ListenableActionFuture}

import scala.concurrent.{Future, Promise}
import scala.language.implicitConversions

/**
  * Marco Ebert 18.05.16
  */
object Helpers {

  /**
    * Implicitly converts a Java ListenableActionFuture into a Scala Future.
    *
    * @param actionFuture Java ListenableActionFuture
    * @tparam T Result type of the ListenableActionFuture
    * @return Scala Future
    */
  implicit def listenableActionFuture2Future[T](actionFuture: ListenableActionFuture[T]): Future[T] = {
    val promise = Promise[T]()

    actionFuture.addListener(new ActionListener[T] {

      override def onFailure(throwable: Throwable): Unit = promise failure throwable

      override def onResponse(response: T): Unit = promise success response

    })

    promise.future
  }

}
