package controllers

import javax.inject.{Inject, Singleton}

import models.Comment
import services.CommentService

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class CommentController @Inject()(override val service: CommentService) extends AbstractController[Comment]
