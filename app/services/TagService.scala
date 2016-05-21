package services

import javax.inject.{Inject, Singleton}

import models.{Index, Tag}
import org.elasticsearch.client.Client

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class TagService @Inject()(client: Client, index: Index) extends AbstractService[Tag](client, index, Tag.Type)
