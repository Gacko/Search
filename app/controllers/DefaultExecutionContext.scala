package controllers

import play.api.mvc.AbstractController

import scala.concurrent.ExecutionContext

/**
  * Marco Ebert 18.02.17
  */
trait DefaultExecutionContext { this: AbstractController =>

  /**
    * Implicitly provide default execution context from AbstractController.
    *
    * @return Execution context.
    */
  protected implicit def executionContext: ExecutionContext = defaultExecutionContext

}
