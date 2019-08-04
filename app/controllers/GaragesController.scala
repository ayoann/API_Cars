package controllers

import java.util.Date

import DAO.Garages
import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.db.Database
import play.libs.Json
import play.api.libs.json._
import play.api.libs.functional.syntax._

@Api
class GaragesController @Inject()(cc: ControllerComponents, db: Database) extends AbstractController(cc) {

  implicit val garagesWrites: Writes[Garages] = (
    (JsPath \ "id").writeNullable[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "adress").write[String] and
      (JsPath \ "creation_date").write[java.util.Date] and
      (JsPath \ "max_cars_capacity").write[Int]
    )(unlift(Garages.unapply))

  implicit val garagesReaders: Reads[Garages] = (
    (JsPath \ "id").read(Int) and
      (JsPath \ "name").read[String] and
      (JsPath \ "adress").read[String] and
      (JsPath \ "creation_date").read[java.util.Date] and
      (JsPath \ "max_cars_capacity").read[Int]
    )(unlift(Garages.unapply))


  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Garages not found")))
  def getOneGaragesById(@ApiParam(value = "ID Garages") id: String) = Action {
    db.withConnection { connection =>
      val stmt = connection.createStatement
      val e = stmt.executeQuery(s"SELECT * FROM GARAGES WHERE id =: $id")
      Ok(Json.toJson(e))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Invalid Request"),
    new ApiResponse(code = 404, message = "Ressource not found")))
  def post = Action {
    Ok("Your new application is ready.")
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Garages not found"),
    new ApiResponse(code = 500, message = "Request Invalid")))
  def putOneGaragesById(@ApiParam(value = "ID Garages") id: String) = Action {
    Ok("Your new application is ready.")
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Garages not found"),
    new ApiResponse(code = 500, message = "Request Invalid")))
  def deleteOneGaragesById(@ApiParam(value = "ID Garages") id: String) = Action {
    Ok("Your new application is ready.")
  }
}
