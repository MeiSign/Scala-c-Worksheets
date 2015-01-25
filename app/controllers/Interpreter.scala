package controllers

import play.api.mvc.{Action, Controller}

//import javax.script.ScriptEngineManager
import scala.tools.nsc._
import scala.tools.nsc.interpreter._

//trait MyType

object Interpreter extends Controller {

//  val interpreter = new ScriptEngineManager().getEngineByName("scala")
//  val settings = interpreter.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings
//  settings.embeddedDefaults[Interpreter]
//  settings.usejavacp.value = true

  def index = Action {
    Ok(views.html.interpreter())
  }

//  def interpret(input: String) = Action {
//    implicit request => interpreter.eval("1 to 10 foreach println")
//      Ok("Got: " + input)
//  }

  def interpret(input: String) = Action { implicit request =>
    val settings = new Settings
    val scalaLibraryPath = "/home/peter/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.10.3.jar"
    settings.bootclasspath.append(scalaLibraryPath)
//    settings.classpath.append(scalaLibraryPath)
//    settings.embeddedDefaults[MyType]
//    settings.usejavacp.value = true
//    settings.deprecation.value = true
    val imain = new IMain(settings)
    imain.interpret("val x = " + input)
    imain.close
    Ok("Res: " + imain.valueOfTerm("x").get)
  }

}