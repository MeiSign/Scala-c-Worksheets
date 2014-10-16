package domain

sealed trait MessageOperation {
  val positon: Int
}

case class DeleteOperation(position: Int) extends MessageOperation {
  override val positon: Int = position
}

case class AddOperation(position: Int, char: Int) extends MessageOperation {
  override val positon: Int = position
}

case class Message(uuid: Uuid, operation: MessageOperation)

