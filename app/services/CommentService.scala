package services

import javax.inject.{Inject, Singleton}

import models.Comment
import org.elasticsearch.client.Client

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class CommentService @Inject()(override val client: Client) extends AbstractService[Comment](Comment.Type)
