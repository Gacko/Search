package actors

import javax.inject.Inject

import akka.actor.Actor
import dao.post.PostDAO
import models.post.Post
import models.post.Posts

/**
  * Marco Ebert 12.12.16
  */
object Indexer {

  /**
    * Actor name.
    */
  final val Name = "indexer"

}

final class Indexer @Inject()(dao: PostDAO) extends Actor {

  import context.dispatcher

  /**
    * Indexes posts.
    */
  override def receive: Receive = {
    // Bulk indexing.
    case Posts(posts) => dao index posts
    // Single post indexing.
    case post: Post => dao index post
  }

}
