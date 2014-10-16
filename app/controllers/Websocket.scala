package controllers

import domain.{Uuid, Username, User}
import scala.concurrent.Future
import actors.UserActor
import play.api.libs.json.JsValue
import play.api.mvc.{Session, Action, Controller, WebSocket}
import play.api.Play.current

object Websocket extends Controller {
  val uuidSessionKey = "uuid"
  val usernameSessionKey = "username"

  def index = Action {
    implicit request => {
      val user = getUserFromSession(request.session).getOrElse(User(genUuid, Username("Hans")))

      Ok(views.html.editor(user)).withSession(request.session +
        (uuidSessionKey -> user.uuid.value) +
        (usernameSessionKey -> user.name.value))
    }
  }

  def ws = WebSocket.tryAcceptWithActor[JsValue, JsValue] {
    implicit request => Future.successful(getUserFromSession(request.session) match {
      case None => Left(Forbidden)
      case Some(user) => Right(UserActor.props(user))
    })
  }

  def getUserFromSession(session: Session): Option[User] = {
    (session.get(uuidSessionKey), session.get(usernameSessionKey)) match {
      case (Some(uuid), Some(name)) => Some(User(Uuid(uuid), Username(name)))
      case _ => None
    }
  }

  def genUuid = Uuid(java.util.UUID.randomUUID.toString)
}
