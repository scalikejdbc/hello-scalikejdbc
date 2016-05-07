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
class Programmers @Inject() (json4s: Json4s) extends Controller {

  import json4s._
  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def all = Action {
    Ok(Extraction.decompose(Programmer.findAll))
  }

  def show(id: Long) = Action {
    Programmer.find(id) match {
      case Some(programmer) => Ok(Extraction.decompose(programmer))
      case _ => NotFound
    }
  }

  case class ProgrammerForm(name: String, companyId: Option[Long] = None)

  private val programmerForm = Form(
    mapping(
      "name" -> text.verifying(nonEmpty),
      "companyId" -> optional(longNumber)
    )(ProgrammerForm.apply)(ProgrammerForm.unapply)
  )

  def create = Action { implicit req =>
    programmerForm.bindFromRequest.fold(
      formWithErrors => BadRequest("invalid parameters"),
      form => {
        val programmer = Programmer.create(name = form.name, companyId = form.companyId)
        Created.withHeaders(LOCATION -> s"/programmers/${programmer.id}")
        NoContent
      }
    )
  }

  def addSkill(programmerId: Long, skillId: Long) = Action {
    Programmer.find(programmerId) match {
      case Some(programmer) =>
        try {
          Skill.find(skillId).foreach(programmer.addSkill)
          Ok
        } catch { case scala.util.control.NonFatal(e) => Conflict }
      case _ => NotFound
    }
  }

  def deleteSkill(programmerId: Long, skillId: Long) = Action {
    Programmer.find(programmerId) match {
      case Some(programmer) =>
        Skill.find(skillId).foreach(programmer.deleteSkill)
        Ok
      case _ => NotFound
    }
  }

  def joinCompany(programmerId: Long, companyId: Long) = Action {
    Company.find(companyId) match {
      case Some(company) =>
        Programmer.find(programmerId) match {
          case Some(programmer) =>
            programmer.copy(companyId = Some(company.id)).save()
            Ok
          case _ => BadRequest("Programmer not found!")
        }
      case _ => BadRequest("Company not found!")
    }
  }

  def leaveCompany(programmerId: Long) = Action {
    Programmer.find(programmerId) match {
      case Some(programmer) =>
        programmer.copy(companyId = None).save()
        Ok
      case _ => BadRequest("Programmer not found!")
    }
  }

  def delete(id: Long) = Action {
    Programmer.find(id) match {
      case Some(programmer) =>
        programmer.destroy()
        NoContent
      case _ => NotFound
    }
  }

}
