package controllers

import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc._
import DAO.{CarsDataRepo, CarsRepo, GaragesDataRepo}
import Model.{Cars, GaragesData}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Api(tags = Array("Garages"))
class GaragesController @Inject()(carsRepo: CarsRepo, carsDataRepo: CarsDataRepo)(implicit ec: ExecutionContext, garagesRepo: GaragesDataRepo, cc: ControllerComponents) extends AbstractController(cc) {

  implicit val garagesWrites = Json.writes[GaragesData]
  implicit val garagesReads = Json.reads[GaragesData]

  implicit val carsWrites = Json.writes[Cars]
  implicit val carsReads = Json.reads[Cars]

  @ApiOperation(nickname = "Get All cars in a garages",
    value = "Get all cars by garages",
    response = classOf[Void],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success", response = classOf[Cars]),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "color", dataType = "string", paramType = "query")))
  def getAllCars(@ApiParam(value = "ID Garages") id: String): Action[AnyContent]  = Action.async { implicit request =>
    val identifier = id.toInt
    request.getQueryString("color") match {
      case None => carsRepo.all(identifier).map(cars => Ok(Json.toJson(cars)))
      case Some(color) => carsRepo.findByColor(color).map(car => Ok(Json.toJson(car)))
    }
  }


  @ApiOperation(nickname = "Get Garages by Id",
    value = "Get oneGarages",
    response = classOf[Void],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success", response = classOf[GaragesData]),
    new ApiResponse(code = 404, message = "Garages not found"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "nbCars", dataType = "boolean", paramType = "query")))
  def getOneGaragesById(@ApiParam(value = "ID Garages") id: String): Action[AnyContent] = Action.async {
    implicit request =>
      val identifier = id.toInt
      request.getQueryString("nbCars") match {
        case None =>
          garagesRepo.findById(identifier).map {
            case None => NotFound(s"Garages $identifier is not found")
            case Some(garage) => Ok(Json.toJson(garage))
          }.recover {
          case ex: Exception => InternalServerError(ex.getCause.getMessage)
        }
        case Some("false") => Future.successful(BadRequest("Method not Allowed"))
        case Some("true") => carsDataRepo.countByGaragesId(identifier).map(c => Ok(s"$c"))
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
    new ApiImplicitParam(value = "Garages object that needs to be added", required = true, dataType = "Model.GaragesData", paramType = "body")))
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
    new ApiImplicitParam(value = "Garages object that needs to be added", required = true, dataType = "Model.GaragesData", paramType = "body")))
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
  def deleteOneGaragesById(@ApiParam(value = "ID garages to delete") id: String): Action[AnyContent] = Action.async {
    val identifier = id.toInt
    for {
      _ <- carsDataRepo.deleteAllCars(identifier)
      garage <- garagesRepo.deleteById(identifier)
    } yield garage
    Future.successful(Ok(s"Garages $identifier is deleted"))
  }
}
