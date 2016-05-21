package controllers

import javax.inject.{Inject, Singleton}

import services.CommentService

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class CommentController @Inject()(service: CommentService) extends AbstractController(service)
