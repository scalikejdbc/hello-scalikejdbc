package controllers

import play.api.mvc._

class Root extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

}
