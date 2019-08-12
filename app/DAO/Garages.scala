package DAO

import java.util.UUID
import akka.http.scaladsl.model.Date

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio
import slick.dbio.Effect.Read
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import dbConfig.profile.api._

case class GaragesData(id: UUID,
                      name: String,
                      adress: String,
                      creation_date: Date,
                      max_cars_capacity: Int)


class GaragesRepo @Inject()(carsRepo: CarsRepo)(protected val dbconfiguration: DatabaseConfigProvider){

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private[models] val Garages = TableQuery[GaragesTable]


  private def _findById(id: Int): DBIO[Option[GaragesData]] =
    Garages.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[GaragesData]] =
    db.run(_findById(id))

  def create(garages: GaragesData): DBIO[Int] {
    db.run(Garages returning Garages.map(_.id) += GaragesData)
  }

  def deleteById(id: Int): DBIO[Int] = {
    val query = _findById(id)

    val interaction = for {
      garages <- query.result
      _       <- DBIO.sequence(garages.map(g => carsRepo._deleteAllInGarages(p.id)))
      garagesDeleted <- query.delete
    } yield garagesDeleted
    db.run(interaction.transationnally)
  }

  def create(garages: GaragesData): DBIO[Int] {
    db.run(Garages returning Garages.map(_.id) += GaragesData)
  }


  private[models] class GaragesTable(tag: Tag) extends Table[GaragesData](tag, "GARAGES") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    def name = column[String]("NAME")
    def adress = column[String]("ADRESS")
    def creation_date = column[Date]("CREATION_DATE")
    def max_cars_capacity = column[Int]("MAX_CARS_CAPACITY")

    def * = (id, name, adress, creation_date, max_cars_capacity) <> (GaragesData, GaragesData.unapply)
    def ? = (id.?, name.?, adress.?, creation_date.?, max_cars_capacity.?).shaped.<>({ r => import r._; _1.map(_ => CarsData.tupled((_1.get, _2.get, _3.get, _4.get, _5.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

}