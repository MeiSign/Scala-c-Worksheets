package domain

sealed trait MessageOperation {
  val version: Long
  val positon: Int
}

case class DeleteOperation(ver: Long, position: Int) extends MessageOperation {
  override val positon: Int = position
  override val version: Long = ver
}

case class AddOperation(ver: Long, position: Int, char: Int) extends MessageOperation {
  override val positon: Int = position
  override val version: Long = ver
}

case class Message(uuid: Uuid, operation: MessageOperation)

object SubscribeMessage
