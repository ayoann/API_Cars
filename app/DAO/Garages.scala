package DAO

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class GaragesData(id: Int,
                      name: String,
                      adress: String,
                      creation_date: Date,
                      max_cars_capacity: Int)

@Singleton
class GaragesRepo @Inject()(carsRepo: CarsRepo)(protected val dbConfigProvider: DatabaseConfigProvider){

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private val Garages = TableQuery[GaragesTable]


  private def _findById(id: Int): DBIO[Option[GaragesData]] =
    Garages.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[GaragesData]] =
    db.run(_findById(id))

  def create(garages: GaragesData): Future[Int] = {
    db.run(Garages returning Garages.map(_.id) += garages)
  }

  /*def deleteById(id: Int): DBIO[Int] = {
    val query = _findById(id)

    val interaction = for {
      garages <- query.result
      _       <- DBIO.sequence(garages.map(g => carsRepo._deleteAllInGarages(g.id)))
      garagesDeleted <- query.delete
    } yield garagesDeleted
    db.run(interaction.transationnally)
  }*/




  private class GaragesTable(tag: Tag) extends Table[GaragesData](tag, "garages") {

    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")
    def adress = column[String]("address")
    def creation_date = column[Date]("creation_date")
    def max_cars_capacity = column[Int]("max_cars_capacity")

    def * = (id, name, adress, creation_date, max_cars_capacity) <> (GaragesData.tupled, GaragesData.unapply)
    def ? = (id.?, name.?, adress.?, creation_date.?, max_cars_capacity.?).shaped.<>({ r => import r._; _1.map(_ => GaragesData.tupled((_1.get, _2.get, _3.get, _4.get, _5.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

}