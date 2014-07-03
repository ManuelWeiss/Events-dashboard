package akka

import akka.actor._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import scala.language.postfixOps
import scala.concurrent.duration._

import controllers.ChatApplication
import org.joda.time.DateTime
import scala.util.Random

object ChatActors {

  /** SSE-Chat actor system */
  val system = ActorSystem("sse-chat")

  /** Supervisor for test user actors */
  val supervisor = system.actorOf(Props(new Supervisor()), "ChatterSupervisor")

  case object Talk
}

/** Supervisor initiating sample event actors and scheduling their talking */
class Supervisor() extends Actor {

  val events = context.actorOf(Props(new Chatter(UserEvents.events)))
  context.system.scheduler.schedule(1 seconds, 0.1 seconds, events, ChatActors.Talk)

  def receive = { case _ => }
}

/** Chat participant actors picking events at random when told to talk */
class Chatter(events: Seq[JsObject]) extends Actor {

  def receive = {
    case ChatActors.Talk  => {
      val event = events(Random.nextInt(events.size))

      ChatApplication.chatChannel.push(event)
    }
  }
}

object UserEvents {
  val events = Seq(
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "Dashboard",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34242423,
          "language" -> "en-GB",
          "returning" -> false,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "Dashboard",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34242423,
          "language" -> "en-GB",
          "returning" -> false,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "Dashboard",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34242423,
          "language" -> "en-GB",
          "returning" -> true,
          "price_id" -> "KA52CM"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "Dashboard",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34242423,
          "language" -> "en-GB",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "404",
            "path" -> "/signup/register",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 4564564,
          "language" -> "en-GB",
          "returning" -> true,
          "price_id" -> "KA52CM"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "404",
            "path" -> "/signup/register",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 4564564,
          "language" -> "en-GB",
          "returning" -> false,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "Pricing",
            "path" -> "/pricing",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34534535,
          "language" -> "en-GB",
          "returning" -> false,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "Dashboard",
            "path" -> "/home",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34534535,
          "language" -> "en-GB",
          "returning" -> false,
          "price_id" -> "KA52CM"
        )
    ),
    Json.obj(
        "stream" -> "registration",
        "page" -> Json.obj (
            "category" -> "About",
            "path" -> "/about",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34534535,
          "language" -> "en-GB",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "login",
        "page" -> Json.obj (
            "category" -> "Home",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 6456456,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "KA52CM"
        )
    ),
    Json.obj(
        "stream" -> "login",
        "page" -> Json.obj (
            "category" -> "Pricing",
            "path" -> "/pricing",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 3453453,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "login",
        "page" -> Json.obj (
            "category" -> "Home",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 5676422,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "52CM1A"
        )
    ),
    Json.obj(
        "stream" -> "site-creation",
        "page" -> Json.obj (
            "category" -> "Home",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 5676422,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "site-creation",
        "page" -> Json.obj (
            "category" -> "Home",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34534535,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "52CM1A"
        )
    ),
    Json.obj(
        "stream" -> "site-creation",
        "page" -> Json.obj (
            "category" -> "Home",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34242423,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "site-creation",
        "page" -> Json.obj (
            "category" -> "Home",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 4546456,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "site-creation",
        "page" -> Json.obj (
            "category" -> "Home",
            "path" -> "/",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34534534,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "subscription",
        "page" -> Json.obj (
            "category" -> "Pricing",
            "path" -> "/pricing",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 5676422,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "subscription",
        "page" -> Json.obj (
            "category" -> "Pricing",
            "path" -> "/pricing",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34534535,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "52CM1A"
        )
    ),
    Json.obj(
        "stream" -> "subscription",
        "page" -> Json.obj (
            "category" -> "Pricing",
            "path" -> "/pricing",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 34242423,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "subscription",
        "page" -> Json.obj (
            "category" -> "Kickstart Home",
            "path" -> "/kickstart",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 4546456,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "52CM1A"
        )
    ),
    Json.obj(
        "stream" -> "subscription",
        "page" -> Json.obj (
            "category" -> "Kickstart Home",
            "path" -> "/kickstart",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 6456456,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "cancellation",
        "page" -> Json.obj (
            "category" -> "Billing",
            "path" -> "/billing",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 0,
          "language" -> "en-US",
          "returning" -> false,
          "price_id" -> "SMT"
        )
    ),
    Json.obj(
        "stream" -> "cancellation",
        "page" -> Json.obj (
            "category" -> "Billing",
            "path" -> "/billing",
            "environment" -> "development"
        ),
        "user" -> Json.obj (
          "userId" -> 353453,
          "language" -> "en-US",
          "returning" -> true,
          "price_id" -> "52CM1A"
        )
    )
  )
}
