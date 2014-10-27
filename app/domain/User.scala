package domain

case class User(uuid: Uuid, name: Username)
case class Uuid(value: String) extends AnyVal
case class Username(value: String) extends AnyVal