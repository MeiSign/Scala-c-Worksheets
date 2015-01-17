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
      case Some("add") => supervisor ! Message((js \ "version").as[Long], user.uuid, AddOperation((js \ "position").as[Int], (js \ "char").as[Int]))
      case Some("delete") => supervisor ! Message((js \ "version").as[Long], user.uuid, DeleteOperation((js \ "position").as[Int]))
      case _ => log.error("unhandled operation: " + js)
    }
    case other => log.error("unhandled: " + other)
  }

  def generateOperationJson(m: Message): JsObject =
    m.operation match {
      case add: AddOperation =>
        Json.obj("version" -> m.version, "type" -> "add", "uuid" -> m.uuid.value, "position" -> add.position, "char" -> add.char)
      case del: DeleteOperation =>
        Json.obj("version" -> m.version, "type" -> "delete", "uuid" -> m.uuid.value, "position" -> del.position)
  }
}

object UserActor {
  def props(user: User)(out: ActorRef) = Props(new UserActor(user, SupervisorActor(), out))
}