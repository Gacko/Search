package services

import javax.inject.{Inject, Singleton}

import models.{Comment, Index}
import org.elasticsearch.client.Client

/**
  * Marco Ebert 21.05.16
  */
@Singleton
final class CommentService @Inject()(client: Client, index: Index) extends AbstractService[Comment](client, index, Comment.Type)
