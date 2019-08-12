package controllers

import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc._
import DAO.{GaragesData, GaragesRepo}
import play.api.libs.json._

import scala.concurrent.ExecutionContext

@Api(tags = Array("Garages"))
class GaragesController @Inject()(implicit ec: ExecutionContext, garagesRepo: GaragesRepo, cc: ControllerComponents) extends AbstractController(cc) {

  implicit val carsWrites = Json.writes[GaragesData]
  implicit val carsReads = Json.reads[GaragesData]

  @ApiOperation(nickname = "Get Garages by Id",
    value = "Get oneGarages",
    response = classOf[Void],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success", response = classOf[GaragesData]),
    new ApiResponse(code = 404, message = "Garages not found"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  def getOneGaragesById(@ApiParam(value = "ID Garages") id: String): Action[AnyContent] = Action.async {
    val identifier = Integer.valueOf(id)
    garagesRepo.findById(identifier).map(garages => Ok(Json.toJson(garages))).recover{
      case ex: Exception => InternalServerError(ex.getCause.getMessage)
    }
  }


  @ApiOperation(nickname = "Add Garages",
    value = "Add a new Garages",
    response = classOf[Void],
    httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Garages is created"),
    new ApiResponse(code = 405, message = "Invalid Input")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Garages object that needs to be added", required = true, dataType = "DAO.GaragesData", paramType = "body")))
  def post = Action { implicit request =>
    /*val body = request.body.validate[GaragesData]

    body.fold(
      invalid => {
        badRequest = Bad_Request("Invalid Garages Json")
      },
      garages => {
        garagesRepo.create(garages).map(id => Ok(s"Garages $id is created"))
      }
    )*/
    Ok("")
  }


  @ApiOperation(nickname = "Update a Garages",
    value = "Update a Garages",
    response = classOf[Void],
    httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Garages is updated"),
    new ApiResponse(code = 400, message = "Invalid garages supplied"),
    new ApiResponse(code = 404, message = "Garages not found")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Garages object that needs to be added", required = true, dataType = "DAO.GaragesData", paramType = "body")))
  def putOneGaragesById(@ApiParam(value = "ID Garages") id: String) = Action {
    Ok("Your new application is ready.")
  }


  @ApiOperation(nickname = "Delete a Garages",
    value = "Delete a Garages",
    response = classOf[Void],
    httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Garages is deleted"),
    new ApiResponse(code = 400, message = "Invalid garages supplied"),
    new ApiResponse(code = 404, message = "Garages not found")))
  def deleteOneGaragesById(@ApiParam(value = "ID garages to delete") id: String): Action[AnyContent] = Action {
    Ok("Your new application is ready.")
  }

}
