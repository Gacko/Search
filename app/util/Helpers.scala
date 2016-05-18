package util

import org.elasticsearch.action.{ActionListener, ListenableActionFuture}

import scala.concurrent.{Future, Promise}

/**
  * Marco Ebert 18.05.16
  */

object Helpers {

  implicit class RichListenableActionFuture[T](actionFuture: ListenableActionFuture[T]) {

    def future: Future[T] = {
      val promise = Promise[T]()

      actionFuture.addListener(new ActionListener[T] {

        override def onFailure(throwable: Throwable): Unit = promise failure throwable

        override def onResponse(response: T): Unit = promise success response

      })

      promise.future
    }

  }

}
