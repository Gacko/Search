package controllers

import javax.inject.{Inject, Singleton}

import models.Tag
import services.TagService

/**
  * Marco Ebert 19.05.16
  */
@Singleton
final class TagController @Inject()(override val service: TagService) extends AbstractController[Tag]
