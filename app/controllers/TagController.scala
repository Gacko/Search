package controllers

import javax.inject.{Inject, Singleton}

import services.TagService

/**
  * Marco Ebert 19.05.16
  */
@Singleton
final class TagController @Inject()(service: TagService) extends AbstractController(service)
