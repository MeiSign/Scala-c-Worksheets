package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.actor.ActorRef
import akka.actor.Terminated
import domain.{SubscribeMessage, Message}
import play.libs.Akka
import akka.actor.Props

class SupervisorActor extends Actor with ActorLogging {
  var users = Set[ActorRef]()

  def receive = LoggingReceive {
    case m: Message => users filter(ref => ref != sender) map(_ ! m)
    case SubscribeMessage =>
      users += sender
      context watch sender
    case Terminated(user) => users -= user
  }
}

object SupervisorActor {
  lazy val supervisor = Akka.system().actorOf(Props[SupervisorActor])

  def apply() = supervisor
}