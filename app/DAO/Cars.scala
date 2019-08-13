package DAO

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CarsData(id: Int,
                    registration: String,
                    brand: String,
                    model: String,
                    color: String,
                    date_commissioning: Date,
                    price: Float)

@Singleton
class CarsRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private val Cars = TableQuery[CarsTable]


  private def _findById(id: Int): DBIO[Option[CarsData]] =
    Cars.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[CarsData]] =
    db.run(_findById(id))

  /*private def _findByName(name: String): Query[CarsTable, CarsData, List] =
    Cars.filter(_.name === name).to[List]*/

  def all: Future[List[CarsData]] =
    db.run(Cars.to[List].result)

  def create(cars: CarsData): Future[Int] = {
    db.run(Cars returning Cars.map(_.id) += cars)
  }

  /*def put(cars: CarsData): DBIO[Int] = {
    val query = Cars.filter(_findById(cars.id))

    val update = query.result.head.flatMap{
      cars => query.update(cars.patch(cars))
    }
    db.run(update)
  }*/

  def deleteById(id: Int): Future[Unit] = {
    db.run(Cars.filter(_.id === id).delete).map(_ => ())
  }

  def deleteAll: Future[Unit] = {
    db.run(Cars.delete).map(_ => ())
  }

  /*def _deleteAllInGarages(garagesId: Int): DBIO[Int] =
    Cars.filter(_.garages === garagesId).delete*/


  /*def options(): Future[Seq[(String, String)]] = {
    val query = (
      for {
        cars <- cars
      } yield (cars.id, cars.name).sortBy(_._2)

      db.run(query.result).map(rows => rows.map{ case (id, name) => (id.toString, name)})
    )
  }*/



  private class CarsTable(tag: Tag) extends Table[CarsData](tag, "CARS") {

    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    def id = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    def registration = column[String]("REGISTRATION")
    def brand = column[String]("BRAND")
    def model = column[String]("MODEL")
    def color = column[String]("COLOR")
    def date_commissioning = column[Date]("date_commissioning")
    def price = column[Float]("PRICE")

    def * = (id, registration, brand, model, color, date_commissioning, price) <> (CarsData.tupled, CarsData.unapply)
    def ? = (id.?, registration.?, brand.?, model.?, color.?, date_commissioning.?, price.?).shaped.<>({ r => import r._; _1.map(_ => CarsData.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }
}