package controllers

import play.api.mvc._
import play.api.routing._

class JsRouter extends Controller {

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        Companies.all,
        Companies.show,
        Companies.create,
        Companies.delete,
        Programmers.all,
        Programmers.show,
        Programmers.create,
        Programmers.addSkill,
        Programmers.deleteSkill,
        Programmers.joinCompany,
        Programmers.leaveCompany,
        Programmers.delete,
        Root.index,
        Skills.all,
        Skills.show,
        Skills.create,
        Skills.delete)).as("text/javascript")
  }

}
