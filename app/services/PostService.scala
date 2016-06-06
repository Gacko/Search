package services

import javax.inject.{Inject, Singleton}

import models.{Comment, Index, Post}
import org.elasticsearch.client.Client

/**
  * Marco Ebert 06.06.16
  */
@Singleton
final class PostService @Inject()(client: Client, index: Index) extends AbstractService[Comment](client, index, Post.Type)
