package controllers

import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc._
import DAO.{CarsData, CarsRepo}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Api(tags = Array("Cars"))
class CarsController @Inject()(implicit ec: ExecutionContext, carsRepo: CarsRepo, cc: ControllerComponents) extends AbstractController(cc) {

  implicit val carsWrites = Json.writes[CarsData]
  implicit val carsReads = Json.reads[CarsData]

  @ApiOperation(nickname = "Get cars by Id",
    value = "Get one Cars",
    response = classOf[Void],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success", response = classOf[CarsData]),
    new ApiResponse(code = 404, message = "Cars not found"),
    new ApiResponse(code = 500, message = "Internal server error")))
  def getOneCardById(@ApiParam(value = "ID Cars") id: String): Action[AnyContent] = Action.async {
    val identifier = Integer.valueOf(id)
    carsRepo.findById(identifier).map {
      case None => NotFound(s"Cars with $id is not found")
      case Some(car) => Ok(Json.toJson(car))
    }.recover{
      case ex: Exception => InternalServerError(ex.getCause.getMessage)
    }
  }

  @ApiOperation(nickname = "Get Cars",
    value = "Get all cars",
    response = classOf[Void],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success", response = classOf[CarsData]),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  def getAllCars: Action[AnyContent] = Action.async {
    carsRepo.all.map(cars => Ok(Json.toJson(cars))).recover{
      case ex: Exception => InternalServerError(ex.getCause.getMessage)
    }
  }


  @ApiOperation(nickname = "Add Cars",
    value = "Add a new Cars",
    response = classOf[Void],
    httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Cars is created"),
    new ApiResponse(code = 400, message = "Invalid Input")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Cars object that needs to be added", required = true, dataType = "DAO.CarsData", paramType = "body")))
  def post: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CarsData].map {
      cars => carsRepo.create(cars)
          .map(_ => Ok(s"${cars.model} is created"))
    }.recoverTotal{
      e => Future.successful(BadRequest(s"Error in json : $e"))
    }
  }


  @ApiOperation(nickname = "Update a Cars",
    value = "Update a Cars",
    response = classOf[Void],
    httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Cars is updated"),
    new ApiResponse(code = 400, message = "Invalid cars supplied"),
    new ApiResponse(code = 404, message = "Cars not found")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Cars object that needs to be added", required = true, dataType = "DAO.CarsData", paramType = "body")))
  def putOneCarsById(@ApiParam(value = "ID Cars") id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CarsData].map {
      cars => carsRepo.update(cars)
        .map {
          case None => NotFound(s"Cars $id is not found")
          case Some(car) => Ok(s"Car ${car.id} is updated")
      }
    }.recoverTotal{
      e => Future.successful(BadRequest(s"Error in json : $e"))
    }
  }


  @ApiOperation(nickname = "Delete a Cars",
    value = "Delete a Cars",
    response = classOf[Void],
    httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Cars is deleted"),
    new ApiResponse(code = 404, message = "Cars not found"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  def deleteOneCarsById(@ApiParam(value = "ID Car") id: String): Action[AnyContent] = Action.async {
    val identifier = Integer.valueOf(id)
    carsRepo.deleteById(identifier)
      .map{
        case None => NotFound(s"Cars $id is not found")
        case Some(car) => Ok(s"Car ${car.id} is deleted")
    }.recover{
      case ex: Exception => InternalServerError(ex.getCause.getMessage)
    }
  }


  @ApiOperation(nickname = "Delete all Cars",
    value = "Delete all Cars",
    response = classOf[Void],
    httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "All cars are deleted"),
    new ApiResponse(code = 400, message = "Invalid garages supplied")))
  def deleteAllCars: Action[AnyContent] = Action.async {
    carsRepo.deleteAll.map(num => Ok(s"$num cars are deleted"))
  }
}