package domain

import play.api.libs.json.{Json, Format}

sealed trait MessageOperation {
  val range: Range
}

case class DeleteOperation(r: Range) extends MessageOperation {
  override val range: Range = r
}

case class AddOperation(r: Range, text: String) extends MessageOperation {
  override val range: Range = r
}
object AddOperation {
  implicit val addOperationFormat: Format[AddOperation] = Json.format[AddOperation]
}
case class AddLinesOperation(r: Range, lines: List[String]) extends MessageOperation {
  override val range: Range = r
}
object AddLinesOperationOperation {
  implicit val addOperationFormat: Format[AddLinesOperation] = Json.format[AddLinesOperation]
}

case class Message(version: Long, uuid: Uuid, operation: MessageOperation)

object SubscribeMessage

case class Range(start: Position, end: Position)
object Range {
  implicit val positionFormat: Format[Position] = Json.format[Position]
  implicit val rangeFormat: Format[Range] = Json.format[Range]
}

case class Position(row: Int, column: Int)

