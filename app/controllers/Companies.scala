package controllers

import play.api._, mvc._
import play.api.data._, Forms._

import org.json4s._, ext.JodaTimeSerializers, native.JsonMethods._
import com.github.tototoshi.play2.json4s.native._

import models._

object Companies extends Controller with Json4s {

  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def all = Action {
    Ok(Extraction.decompose(Company.findAll))
  }

  def show(id: Long) = Action {
    Company.find(id).map { company => Ok(Extraction.decompose(company)) } getOrElse NotFound
  }

  private val companyForm = Form(tuple("name" -> text, "url" -> text))

  def create = Action { implicit req =>
    val (name, url) = companyForm.bindFromRequest.get
    val company = Company.create(name = name, url = if (url.isEmpty) None else Some(url))
    Created.withHeaders(LOCATION -> s"/companies/${company.id}")
  }

  def delete(id: Long) = Action {
    Company.find(id).map { company =>
      company.destroy()
      NoContent
    } getOrElse NotFound
  }

}
