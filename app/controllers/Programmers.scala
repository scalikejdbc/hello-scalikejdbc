package controllers

import play.api._, mvc._
import play.api.data._, Forms._, validation.Constraints._

import org.json4s._, ext.JodaTimeSerializers, native.JsonMethods._
import com.github.tototoshi.play2.json4s.native._

import models._

object Programmers extends Controller with Json4s {

  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def all = Action {
    Ok(Extraction.decompose(Programmer.findAll))
  }

  def show(id: Long) = Action {
    Programmer.find(id).map(programmer => Ok(Extraction.decompose(programmer))) getOrElse NotFound
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
    Programmer.find(programmerId).map { programmer =>
      try {
        Skill.find(skillId).map(skill => programmer.addSkill(skill))
        Ok
      } catch { case e: Exception => Conflict }
    } getOrElse NotFound
  }

  def deleteSkill(programmerId: Long, skillId: Long) = Action {
    Programmer.find(programmerId).map { programmer =>
      Skill.find(skillId).map(skill => programmer.deleteSkill(skill))
      Ok
    } getOrElse NotFound
  }

  def joinCompany(programmerId: Long, companyId: Long) = Action {
    Company.find(companyId).map { company =>
      Programmer.find(programmerId).map { programmer =>
        programmer.copy(companyId = Some(company.id)).save()
        Ok
      } getOrElse BadRequest("Programmer not found!")
    } getOrElse BadRequest("Company not found!")
  }

  def leaveCompany(programmerId: Long) = Action {
    Programmer.find(programmerId).map { programmer =>
      programmer.copy(companyId = None).save()
      Ok
    } getOrElse BadRequest("Programmer not found!")
  }

  def delete(id: Long) = Action {
    Programmer.find(id).map { programmer =>
      programmer.destroy()
      NoContent
    } getOrElse NotFound
  }

}
