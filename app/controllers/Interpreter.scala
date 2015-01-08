package controllers

import play.api.mvc.{Action, Controller}

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.IMain

object Interpreter extends Controller {

  def index = Action {
    Ok(views.html.interpreter())
  }

  def interpret(input: String) = Action { implicit request =>
    val settings = new Settings
    settings.usejavacp.value = true
    settings.deprecation.value = true
    val imain = new IMain(settings)
    imain.interpret("val x = 1")
    println(imain.valueOfTerm("x"))
    imain.close
    Ok("Got: " + input)
  }

}
