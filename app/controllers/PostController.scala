package controllers

import javax.inject.{Inject, Singleton}

import services.PostService

/**
  * Marco Ebert 06.06.16
  */
@Singleton
final class PostController @Inject()(service: PostService) extends AbstractController(service)
