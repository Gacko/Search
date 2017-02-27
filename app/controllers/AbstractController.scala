package controllers

import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext

/**
  * Marco Ebert 27.02.17
  */
abstract class AbstractController(components: ControllerComponents) extends play.api.mvc.AbstractController(components) {

  /**
    * Implicitly provide default execution context from AbstractController.
    *
    * @return Execution context.
    */
  protected implicit def executionContext: ExecutionContext = defaultExecutionContext

}
