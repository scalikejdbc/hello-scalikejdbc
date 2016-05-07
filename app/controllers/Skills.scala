package controllers

import javax.inject.{ Inject, Singleton }
import com.github.tototoshi.play2.json4s.native._
import models._
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.mvc._

@Singleton
class Skills @Inject() (json4s: Json4s) extends Controller {

  import json4s._
  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def all = Action {
    Ok(Extraction.decompose(Skill.findAll))
  }

  def show(id: Long) = Action {
    Skill.find(id) match {
      case Some(skill) => Ok(Extraction.decompose(skill))
      case _ => NotFound
    }
  }

  case class SkillForm(name: String)

  private val skillForm = Form(
    mapping("name" -> text.verifying(nonEmpty))(SkillForm.apply)(SkillForm.unapply)
  )

  def create = Action { implicit req =>
    skillForm.bindFromRequest.fold(
      formWithErrors => BadRequest("invalid parameters"),
      form => {
        val skill = Skill.create(name = form.name)
        Created.withHeaders(LOCATION -> s"/skills/${skill.id}")
        NoContent
      }
    )
  }

  def delete(id: Long) = Action {
    Skill.find(id) match {
      case Some(skill) =>
        skill.destroy()
        NoContent
      case _ => NotFound
    }
  }

}
