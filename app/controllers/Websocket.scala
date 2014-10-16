package controllers

import domain.{Uuid, Username, User}
import play.api.Logger

import scala.concurrent.Future

import actors.UserActor
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc.{Session, Action, Controller, WebSocket}

object Websocket extends Controller {
  val UUID = "uuid"
  val username = "username"

  def index = Action {
    implicit request => {
      val user = getUserFromSession(request.session).getOrElse(User(genUuid, Username("Hans")))

      Ok(views.html.chat(user)).withSession(request.session + (UUID -> user.uuid.value) + (username -> user.name.value))
    }
  }

  def ws = WebSocket.tryAcceptWithActor[JsValue, JsValue] {
    implicit request =>

      Future.successful(getUserFromSession(request.session) match {
      case None => Left(Forbidden)
      case Some(user) => Right(UserActor.props(user))
    })
  }

  def getUserFromSession(session: Session): Option[User] = {
    (session.get(UUID), session.get(username)) match {
      case (Some(uuid), Some(name)) => Some(User(Uuid(uuid), Username(name)))
      case _ => None
    }
  }

  def genUuid = Uuid(java.util.UUID.randomUUID.toString)
}
