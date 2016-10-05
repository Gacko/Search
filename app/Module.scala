import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

import actors.CrawlerActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import dao.ElasticPostDAO
import dao.PostDAO
import play.api.inject.ApplicationLifecycle
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Marco Ebert 24.09.16
  */
@Singleton
sealed class Schedule @Inject()(system: ActorSystem, @Named(CrawlerActor.Name) crawler: ActorRef, lifecycle: ApplicationLifecycle) {

  {
    // Get scheduler.
    val scheduler = system.scheduler
    // Delay schedules by 10 seconds.
    val delay = 10.seconds

    // Start crawling.
    val task = scheduler.scheduleOnce(delay, crawler, CrawlerActor.Start)

    // Cancel tasks on application stop.
    lifecycle addStopHook { () =>
      Future {
        task.cancel()
      }
    }
  }

}

final class Module extends AbstractModule with AkkaGuiceSupport {

  /**
    * Configure bindings.
    */
  override def configure(): Unit = {
    // Bind PostDAO to ElasticPostDAO
    bind(classOf[PostDAO]) to classOf[ElasticPostDAO]
    // Bind CrawlerActor.
    bindActor[CrawlerActor](CrawlerActor.Name)
    // Bind StartStop as eager singleton.
    bind(classOf[Schedule]).asEagerSingleton()
  }

}
