package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.interpreter.Results._
import javax.script.ScriptException
import play.api.Routes

object Interpreter extends Controller {

  private val writer = new java.io.StringWriter
  private val code = MyHelpers.Code("val x = 1")
  private val compiler = MyHelpers.Compiler("")

  def init = Action {
    Ok(views.html.interpreter(compiler))
  }

  def interpret = Action { implicit request =>
    // setup interpreter
    val settings = new Settings
    settings.usejavacp.value = false
    settings.deprecation.value = true
    var imain = new IMain(settings)
    
    // interpret code
    writer.getBuffer.setLength(0)
    val holder = new MyHelpers.ResultHolder(null)
    imain.bind("$result__",holder.getClass.getName, holder)
    val ir = imain.interpret(code.input);
//    ir match {
//      case Success => holder.value
//      case Error => throw new ScriptException("error in: '" + code.input + "'\n" + writer toString)
//      case Incomplete => throw new ScriptException("incomplete in :'" + code.input + "'\n" + writer toString)
//    }
    Ok(views.html.interpreter(compiler))
  }

}

object MyHelpers {
  case class Code(var input: String)
  case class Compiler(var output: String)
  case class ResultHolder(var value: Any)
}