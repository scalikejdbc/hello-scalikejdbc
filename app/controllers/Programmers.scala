package controllers

import play.api._, mvc._
import play.api.data._, Forms._

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

  private val programmerForm = Form(tuple("name" -> text, "companyId" -> optional(longNumber)))

  def create = Action { implicit req =>
    val (name, companyId) = programmerForm.bindFromRequest.get
    val programmer = Programmer.create(name = name, companyId = companyId)
    Created.withHeaders(LOCATION -> s"/programmers/${programmer.id}")
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

  def delete(id: Long) = Action {
    Programmer.find(id).map { programmer =>
      programmer.destroy()
      NoContent
    } getOrElse NotFound
  }

}
