package domain

import play.api.libs.json.{Json, Format}

sealed trait MessageOperation {
  val range: Range
  def addOffset(offset: Offset)
}

case class DeleteOperation(r: Range) extends MessageOperation {
  override val range: Range = r

  def addOffset(offset: Offset) = {
    val newStartPosition = Position(range.start.row + offset.row, range.start.column + offset.col)
    val newEndPosition = Position(range.end.row + offset.row, range.end.column + offset.col)

    val newRange = Range(newStartPosition, newEndPosition)
    this.copy(r = newRange)
  }
}

case class AddOperation(r: Range, text: String) extends MessageOperation {
  override val range: Range = r

  def addOffset(offset: Offset) = {
    val newStartPosition = Position(range.start.row + offset.row, range.start.column + offset.col)
    val newEndPosition = Position(range.end.row + offset.row, range.end.column + offset.col)

    val newRange = Range(newStartPosition, newEndPosition)
    this.copy(r = newRange)
  }
}
object AddOperation {
  implicit val addOperationFormat: Format[AddOperation] = Json.format[AddOperation]
}
case class AddLinesOperation(r: Range, lines: List[String]) extends MessageOperation {
  override val range: Range = r

  def addOffset(offset: Offset) = {
    val newStartPosition = Position(range.start.row + offset.row, range.start.column + offset.col)
    val newEndPosition = Position(range.end.row + offset.row, range.end.column + offset.col)

    val newRange = Range(newStartPosition, newEndPosition)
    this.copy(r = newRange)
  }
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

