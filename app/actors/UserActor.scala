package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import akka.actor.ActorRef
import akka.actor.Props
import scala.xml.Utility


class UserActor(uid: String, supervisor: ActorRef, out: ActorRef) extends Actor with ActorLogging {

  override def preStart() = {
    SupervisorActor() ! Subscribe
  }

  def receive = LoggingReceive {
    case Message(muid, s) if sender == supervisor => {
      val js = Json.obj("type" -> "message", "uid" -> muid, "msg" -> s)
      out ! js
    }
    case js: JsValue => (js \ "msg").validate[String] map { Utility.escape(_) }  map { supervisor ! Message(uid, _ ) }
    case other => log.error("unhandled: " + other)
  }
}

object UserActor {
  def props(uid: String)(out: ActorRef) = Props(new UserActor(uid, SupervisorActor(), out))
}

case class Message(uuid: String, s: String)
object Subscribe
