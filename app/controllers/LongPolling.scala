package controllers

import play.api.libs.EventSource
import play.api.libs.iteratee.{Enumeratee, Concurrent}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global

object LongPolling extends Controller {
  def view = Action { Ok(views.html.index("Test")) }

  def postCursorPosition = Action(parse.json) {
    req => CursorEvent.worksheet.push(req.body)
    Ok
  }

  def filterPositions(worksheet: String) = Enumeratee.filter[JsValue] {
    json: JsValue => (json \ "worksheet").as[String] == worksheet
  }

  def cursorPositionFeed(worksheet: String) = Action {
    Ok.chunked(CursorEvent.out &> filterPositions(worksheet) &> EventSource()).as("text/event-stream")
  }
}

object CursorEvent {
  val (out, worksheet) = Concurrent.broadcast[JsValue]
}

