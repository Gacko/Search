package controllers

import javax.inject.Inject

import services.TagIndexService

/**
  * Marco Ebert 20.05.16
  */
final class TagIndexController @Inject()(override val service: TagIndexService) extends AbstractIndexController
