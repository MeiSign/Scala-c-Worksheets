package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import domain._
import play.api.libs.json.{JsObject, JsValue, Json}
import akka.actor.ActorRef
import akka.actor.Props

class UserActor(user: User, supervisor: ActorRef, out: ActorRef) extends Actor with ActorLogging {

  override def preStart() = SupervisorActor() ! SubscribeMessage

  def receive = LoggingReceive {
    case m: Message if sender == supervisor =>
      val js = generateOperationJson(m)
      out ! js
    case js: JsValue => (js \ "type").asOpt[String] match {
      case Some("insert") => supervisor ! Message((js \ "version").as[Long], user.uuid, AddOperation((js \ "range").as[Range], (js \ "text").as[String]))
      case Some("delete") => supervisor ! Message((js \ "version").as[Long], user.uuid, DeleteOperation((js \ "range").as[Range]))
      case _ => log.error("unhandled operation: " + js)
    }
    case other => log.error("unhandled: " + other)
  }

  def generateOperationJson(m: Message): JsValue = {
    m.operation match {
      case add: AddOperation =>
        Json.obj("version" -> m.version, "type" -> "insert", "uuid" -> m.uuid.value, "range" -> add.range, "text" -> add.text)
      case del: DeleteOperation =>
        Json.obj("version" -> m.version, "type" -> "delete", "uuid" -> m.uuid.value, "range" -> del.range)
    }
  }
}

object UserActor {
  def props(user: User)(out: ActorRef) = Props(new UserActor(user, SupervisorActor(), out))
}