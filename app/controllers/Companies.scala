package controllers

import com.github.tototoshi.play2.json4s.native._
import models._
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.mvc._

class Companies extends Controller with Json4s {

  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def all = Action {
    Ok(Extraction.decompose(Company.findAll))
  }

  def show(id: Long) = Action {
    Company.find(id) match {
      case Some(company) => Ok(Extraction.decompose(company))
      case _ => NotFound
    }
  }

  case class CompanyForm(name: String, url: Option[String] = None)

  private val companyForm = Form(
    mapping(
      "name" -> text.verifying(nonEmpty),
      "url" -> optional(text)
    )(CompanyForm.apply)(CompanyForm.unapply)
  )

  def create = Action { implicit req =>
    companyForm.bindFromRequest.fold(
      formWithErrors => BadRequest("invalid parameters"),
      form => {
        val company = Company.create(name = form.name, url = form.url)
        Created.withHeaders(LOCATION -> s"/companies/${company.id}")
        NoContent
      }
    )
  }

  def delete(id: Long) = Action {
    Company.find(id) match {
      case Some(company) =>
        company.destroy()
        NoContent
      case _ => NotFound
    }
  }

}
