package controllers

import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

@Api
class GaragesController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Garages not found")))
  def getOneGaragesById(@ApiParam(value = "ID Garages") id: String) = Action {
    Ok("Your new application is ready.")
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
