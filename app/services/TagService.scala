package services

import javax.inject.{Inject, Singleton}

import models.Tag
import org.elasticsearch.client.Client

/**
  * Marco Ebert 20.05.16
  */
@Singleton
final class TagService @Inject()(override val client: Client) extends AbstractService[Tag](Tag.Type)
