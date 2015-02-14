package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.actor.ActorRef
import akka.actor.Terminated
import domain._
import play.libs.Akka
import akka.actor.Props

import scala.collection.mutable.ListBuffer

class SupervisorActor extends Actor with ActorLogging {
  var users = Set[ActorRef]()
  var history = ListBuffer[Message]()

  def receive = LoggingReceive {
    case m: Message => {
        val transformedMessage = transform(m)
        users filter(ref => ref != sender) map(_ ! transformedMessage)
        history += transformedMessage
    }
    case SubscribeMessage =>
      users += sender
      context watch sender
      history map(message => sender ! message)
    case Terminated(user) => users -= user
  }

  def transform(message: Message): Message = {
    val endRow = message.operation.range.end.row
    val startRow = message.operation.range.start.row
    val endCol = message.operation.range.end.column
    val startCol = message.operation.range.start.column

    val relevantHistory = history.filter(m => m.version >= message.version)
    val offset: Offset = relevantHistory.foldLeft(Offset(0,0)) {
      (acc, message) => {
        message.operation match {
          case addOp: AddOperation =>
            if ((addOp.range.start.row < startRow) && (addOp.range.start.row < addOp.range.end.row)) Offset(acc.row + 1, acc.col)
            else if ((addOp.range.start.row == startRow) && (addOp.range.start.column <= startCol)) {
              if (addOp.text == "\n") Offset(acc.row + 1, acc.col - startCol)
              else Offset(acc.row, acc.col + addOp.text.length)
            } else Offset(acc.row, acc.col)
          case addLinesOp: AddLinesOperation =>
            if (addLinesOp.range.end.row <= endRow) Offset(acc.row + addLinesOp.lines.length, acc.col)
            else Offset(acc.row, acc.col)
          case delOp: DeleteOperation =>
            if ((delOp.range.end.row < startRow) && (delOp.range.start.row < delOp.range.end.row)) Offset(acc.row - (delOp.range.end.row - delOp.range.start.row), acc.col)
            else if ((delOp.range.start.row == startRow) && (delOp.range.start.column <= startCol) && (delOp.range.start.row == delOp.range.end.row)) Offset(acc.row, acc.col - (delOp.range.end.column - delOp.range.start.column))
            else Offset(acc.row, acc.col)
        }
      }
    }

    message
  }
}

object SupervisorActor {
  lazy val supervisor = Akka.system().actorOf(Props[SupervisorActor])

  def apply() = supervisor
}