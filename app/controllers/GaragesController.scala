package controllers

import java.sql.{Connection, ResultSet}

import DAO.Garages
import io.swagger.annotations._
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.db.Database
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

@Api(description = "Endpoint for get some informations about garages", tags = Array("Garages"))
class GaragesController @Inject()(cc: ControllerComponents, db: Database) extends AbstractController(cc) {

  implicit val garagesReaders = Json.reads[Garages]
  implicit val garagesWrites = Json.writes[Garages]


  val SELECT_ONE_GARAGES = "SELECT * FROM garages where id = ?;"

  @ApiOperation(nickname = "Get Garages by Id",
    value = "Get oneGarages",
    response = classOf[Void],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success", response = classOf[Garages]),
    new ApiResponse(code = 400, message = "Invalid ID"),
    new ApiResponse(code = 404, message = "Garages not found")))
  def getOneGaragesById(@ApiParam(value = "ID Garages") id: String) = Action {
    db.withConnection { connection: Connection =>
      Try {
        val query = SELECT_ONE_GARAGES
        val stmt = connection.prepareStatement(query)
        stmt.setObject(1, id)
        val rs = stmt.executeQuery()
        if(rs.next()) {
          resultSetGarages(rs)
        } else {
          None
        }
      } match {
        case Success(t) => Ok(Json.toJson(t))
        case Failure(e) => Ok(e.getMessage)
      }
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
    new ApiImplicitParam(value = "Garages object that needs to be added", required = true, dataType = "DAO.Garages", paramType = "body")))
  def post = Action { implicit request =>
    request.body
    Ok("Your new application is ready.")
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
    new ApiImplicitParam(value = "Garages object that needs to be added", required = true, dataType = "DAO.Garages", paramType = "body")))
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
  def deleteOneGaragesById(@ApiParam(value = "ID garages to delete") id: String) = Action {
    Ok("Your new application is ready.")
  }


  private def resultSetGarages(resultSet: ResultSet): Option[Garages] = {
    Option(
      Garages(
      resultSet.getInt("id"),
      resultSet.getString("name"),
      resultSet.getString("address"),
      resultSet.getDate("creation_date"),
      resultSet.getInt("max_cars_capacity")
    ))
  }
}
