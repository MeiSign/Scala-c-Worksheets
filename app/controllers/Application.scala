package controllers

import controllers.Interpreter._
import play.api._
import play.api.mvc._
import controllers.Interpreter._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def status = Action {
    Ok("Ok")
  }

  // Javascript routing
  def javascriptRoutes = Action {
    implicit request =>
      import routes.javascript._
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          Interpreter.interpret
        )
      ).as("text/javascript")
  }

}