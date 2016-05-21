package controllers

import javax.inject.Inject

import services.TagIndexService

import scala.concurrent.ExecutionContext

/**
  * Marco Ebert 20.05.16
  */
class TagIndexController @Inject()(override val service: TagIndexService, override val context: ExecutionContext) extends AbstractIndexController
