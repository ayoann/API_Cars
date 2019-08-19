package DAO

import java.util.Date

import Model.CarsData
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{JdbcProfile, TransactionIsolation}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class CarsDataRepo @Inject()(garagesRepo: GaragesDataRepo)(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  val CarsDataTable = TableQuery[CarsDataTable]

  def countByGaragesId(garagesId : Int): Future[Int] =
    db.run(CarsDataTable.filter(_.garagesId === garagesId).length.result)

  def create(cars: CarsData): Future[String] = {
    val capacity = garagesRepo.Garages.filter(_.id === cars.garagesId).map(capacity => capacity.max_cars_capacity)
    val count = CarsDataTable.filter(_.garagesId === cars.garagesId).length
    if(count != capacity) {
      db.run(CarsDataTable returning CarsDataTable.map(_.model) += cars)
    } else {
      Future("Capacity is reached")
    }
  }

  def update(cars: CarsData, id: String): Future[Option[CarsData]] = {
    val query = CarsDataTable.filter(_.id === id.toInt)
    val action = for {
      results <- query.result.headOption
      _ <- query.update(cars)
    } yield results
    db.run(action.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }

  def deleteById(id: Int): Future[Option[CarsData]] = {
    val query = CarsDataTable.filter(_.id === id)
    val action = for {
      results <- query.result.headOption
      _       <- query.delete
    } yield results
    db.run(action.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }

  def deleteAllCars(garagesId: Int): Future[Int] = {
    val query = CarsDataTable.filter(_.garagesId === garagesId).to[List]
    val action = for {
      _              <- query.result.headOption
      queryDelete    <- query.delete
    } yield queryDelete
    db.run(action.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }

  /*def options(): Future[Seq[(String, String)]] = {
    val query = (
      for {
        cars <- cars
      } yield (cars.id, cars.name).sortBy(_._2)

      db.run(query.result).map(rows => rows.map{ case (id, name) => (id.toString, name)})
    )
  }*/

  class CarsDataTable(tag: Tag) extends Table[CarsData](tag, "cars") {

    implicit val DateTimeColumeType =  MappedColumnType.base[java.util.Date,java.sql.Timestamp](
      { d => java.sql.Timestamp.from( d.toInstant) },
      { t => Date.from(t.toInstant) }
    )

    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def registration = column[String]("registration")
    def brand = column[String]("brand")
    def model = column[String]("model")
    def color = column[String]("color")
    def date_commissioning = column[Date]("date_commissioning")
    def price = column[Float]("price")
    def garagesId = column[Int]("id_garages")

    def garage = foreignKey("garages_fk", garagesId, CarsDataTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def * = (registration, brand, model, color, date_commissioning, price, garagesId) <> (CarsData.tupled, CarsData.unapply)
    def ? = (registration.?, brand.?, model.?, color.?, date_commissioning.?, price.?, garagesId.?).shaped.<>({ r => import r._; _1.map(_ => CarsData.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }
}