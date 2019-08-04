package controllers

import java.util.Date

import DAO.Cars
import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import play.libs.Json
import play.api.libs.json._
import play.api.libs.functional.syntax._

@Api
class CarsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  implicit val carsWrites: Writes[Cars] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "registration").write[String] and
      (JsPath \ "brand").write[String] and
      (JsPath \ "model").write[String] and
      (JsPath \ "color").write[String] and
      (JsPath \ "date_commissioning").write[java.util.Date] and
      (JsPath \ "price").write[Float]
    )(unlift(Cars.unapply))

  implicit val carsReaders: Reads[Cars] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "registration").read[String] and
      (JsPath \ "brand").read[String] and
      (JsPath \ "model").read[String] and
      (JsPath \ "color").read[String] and
      (JsPath \ "date_commissioning").read[java.util.Date] and
      (JsPath \ "price").read[Float]
    )(unlift(Cars.unapply))




  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Cars not found")))
  def getOneCardById(@ApiParam(value = "ID Cars") id: String) = Action {
    Ok("Your new application is ready.")
  }

  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Invalid Request"),
    new ApiResponse(code = 404, message = "Cars not found")))
  def getAllCars = Action {
    Ok("Your new application is ready.")
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 500, message = "Request Invalid")))
  def post = Action {
    Ok("Your new application is ready.")
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Cars not found")))
  def putOneCarsById(@ApiParam(value = "ID Cars") id: String) = Action {
    Ok("Your new application is ready.")
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Cars not found"),
    new ApiResponse(code = 500, message = "Request Invalid")))
  def deleteOneCarsById(@ApiParam(value = "ID Car") id: String) = Action {
    Ok("Your new application is ready.")
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid Ressource"),
    new ApiResponse(code = 500, message = "Request Invalid")))
  def deleteAllCars = Action {
    Ok("Your new application is ready.")
  }
}
