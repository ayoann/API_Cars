package controllers

import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc._
import DAO.GaragesRepo
import scala.concurrent.ExecutionContext
import dbConfig.profile.api._

import scala.concurrent.ExecutionContext

@Api
class GaragesController @Inject()(implicit ec: ExecutionContext, garagesRepo: GaragesRepo, cc: ControllerComponents) extends AbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Garages not found")))
  def getOneGaragesById(@ApiParam(value = "ID Garages") id: String) = Action {
    val identifier = Integer.valueOf(id)
    garagesRepo.findById(identifier).map(garages => Ok(Json.toJson(garages))).recover{
      case ex: Exception => InternalServerError(ex.getCause.getMessage)
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Invalid Request"),
    new ApiResponse(code = 404, message = "Ressource not found")))
  def post = Action { implicit request =>
    val body = request.body.validate[GaragesData]

    body.fold(
      invalid => {
        badRequest = Bad_Request("Invalid Garages Json")
      },
      garages => {
        garagesRepo.create(garages).map(id => Ok(s"Garages $id is created"))
      }
    )
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
    val identifier = Integer.valueOf(id)
    garagesRepo.deleteById(identifier).map(num => Ok(s"$num cars is deleted"))
  }

}
