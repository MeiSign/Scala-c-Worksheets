package controllers

import play.api.mvc.{Action, Controller}
import javax.script.ScriptEngineManager

class Interpreter extends Controller {

  val interpreter = new ScriptEngineManager().getEngineByName("scala")
  val settings = interpreter.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings
  settings.embeddedDefaults[Interpreter]
  settings.usejavacp.value = true

  def index = Action {
    Ok(views.html.interpreter())
  }

  def interpret(input: String) = Action { implicit request =>
    interpreter.eval("1 to 10 foreach println")
    Ok("Got: " + input)
  }
}

object Interpreter