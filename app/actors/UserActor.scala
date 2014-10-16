package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import domain.User
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import akka.actor.ActorRef
import akka.actor.Props
import scala.xml.Utility


class UserActor(user: User, supervisor: ActorRef, out: ActorRef) extends Actor with ActorLogging {

  override def preStart() = {
    SupervisorActor() ! Subscribe
  }

  def receive = LoggingReceive {
    case Message(muid, s) if sender == supervisor => {
      val js = Json.obj("type" -> "message", "uid" -> muid, "msg" -> s)
      out ! js
    }
    case js: JsValue => (js \ "msg").validate[String] map { Utility.escape }  map { supervisor ! Message(user.uuid.value, _ ) }
    case other => log.error("unhandled: " + other)
  }
}

object UserActor {
  def props(user: User)(out: ActorRef) = Props(new UserActor(user, SupervisorActor(), out))
}

case class Message(uuid: String, s: String)
object Subscribe
