package controllers

import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc._
import DAO.CarsRepo
import scala.concurrent.ExecutionContext
import dbConfig.profile.api._

@Api
class CarsController @Inject()(implicit ec: ExecutionContext, carsRepo: CarsRepo, cc: ControllerComponents) extends AbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Cars not found")))
  def getOneCardById(@ApiParam(value = "ID Cars") id: String) = Action {
    val identifier = Integer.valueOf(id)
    carsRepo.findById(identifier).map(cars => Ok(Json.toJson(cars)))
  }

  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Invalid Request"),
    new ApiResponse(code = 404, message = "Cars not found")))
  def getAllCars = Action {
    carsRepo.all.map(cars => Ok(Json.toJson(cars)))
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 500, message = "Request Invalid")))
  def post = Action { implicit request =>
    val body = request.body.validate[CarsData]

    body.fold(
      invalid => {
        badRequest = Bad_Request("Invalid Cars Json")
      },
      cars => {
        carsRepo.create(cars).map(id => Ok(s"Cars $id is created"))
      }
    )
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
    val identifier = Integer.valueOf(id)
    carsRepo.deleteById(identifier).map(num => Ok(s"$num cars is deleted"))
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid Ressource"),
    new ApiResponse(code = 500, message = "Request Invalid")))
  def deleteAllCars = Action {
    carsRepo.delete.map(num => Ok(s"$num cars are deleted"))
  }

}
