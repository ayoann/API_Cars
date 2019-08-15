package controllers

import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc._
import DAO.{GaragesData, GaragesRepo}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Api(tags = Array("Garages"))
class GaragesController @Inject()(implicit ec: ExecutionContext, garagesRepo: GaragesRepo, cc: ControllerComponents) extends AbstractController(cc) {

  implicit val garagesWrites = Json.writes[GaragesData]
  implicit val garagesReads = Json.reads[GaragesData]

  @ApiOperation(nickname = "Get Garages by Id",
    value = "Get oneGarages",
    response = classOf[Void],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success", response = classOf[GaragesData]),
    new ApiResponse(code = 404, message = "Garages not found"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  def getOneGaragesById(@ApiParam(value = "ID Garages") id: String): Action[AnyContent] = Action.async {
    val identifier = id.toInt
    garagesRepo.findById(identifier)
      .map {
        case None => NotFound(s"Garages $identifier is not found")
        case Some(garage) => Ok(Json.toJson(garage))
      }.recover {
      case ex: Exception => InternalServerError(ex.getCause.getMessage)
    }
  }


  @ApiOperation(nickname = "Add Garages",
    value = "Add a new Garages",
    response = classOf[Void],
    httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Garages is created"),
    new ApiResponse(code = 400, message = "Invalid Input")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Garages object that needs to be added", required = true, dataType = "DAO.GaragesData", paramType = "body")))
  def post: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[GaragesData].map {
      garages => garagesRepo.create(garages)
          .map(_ => Ok(s"${garages.name} is created"))
    }.recoverTotal {
      e => Future.successful(BadRequest(s"Error in json : $e"))
    }
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
  def putOneGaragesById(@ApiParam(value = "ID Garages") id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[GaragesData].map {
      garages => garagesRepo.update(garages, id)
        .map{
          case None => NotFound(s"Garage $id is not found")
          case Some(_)=> Ok(s"Garage $id is updated")
        }
    }.recoverTotal{
      e => Future.successful(BadRequest(s"Error in json : $e"))
    }
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
    /*val identifier = id.toInt
    garagesRepo.deleteById(identifier).map{
      case None => NotFound(s"Garages $identifier is not found")
      case Some(_) => Ok(s"Garage $identifier id deleted")
    }*/
    Ok("")
  }
}
