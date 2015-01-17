package domain

import play.api.libs.json.{Json, Format}

case class User(uuid: Uuid, name: Username)
case class Uuid(value: String) extends AnyVal
object Uuid {
  implicit  val uuidFormat: Format[Uuid] = Json.format[Uuid]
}
case class Username(value: String) extends AnyVal