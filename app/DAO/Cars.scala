package DAO

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{JdbcProfile, TransactionIsolation}

import scala.concurrent.{ExecutionContext, Future}

case class CarsData(registration: String,
                    brand: String,
                    model: String,
                    color: String,
                    date_commissioning: Date,
                    price: Float,
                    garagesId: Int)

@Singleton
class CarsRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  val Cars = TableQuery[CarsTable]


  private def _findById(id: Int): DBIO[Option[CarsData]] =
    Cars.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[CarsData]] =
    db.run(_findById(id))

  def findByColor(color: String): Future[List[CarsData]] =
    db.run(Cars.to[List].filter(_.color === color).result)

  def all: Future[List[CarsData]] =
    db.run(Cars.to[List].result)

  def create(cars: CarsData): Future[Int] = {
    db.run(Cars returning Cars.map(_.id) += cars)
  }

  def update(cars: CarsData, id: String): Future[Option[CarsData]] = {
    val query = Cars.filter(_.id === id.toInt)
    val action = for {
      results <- query.result.headOption
      _ <- query.update(cars)
    } yield results
    db.run(action.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }

  def deleteById(id: Int): Future[Option[CarsData]]= {
    val query = Cars.filter(_.id === id)
    val action = for {
      results <- query.result.headOption
      _ <- query.delete
    } yield results
    db.run(action.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }

  def deleteAll: Future[Int] = {
    db.run(Cars.delete)
  }

  def _deleteAllInGarages(garagesId: Int): DBIO[Int] = {
    Cars.filter(_.garagesId === garagesId).delete
  }

  /*def options(): Future[Seq[(String, String)]] = {
    val query = (
      for {
        cars <- cars
      } yield (cars.id, cars.name).sortBy(_._2)

      db.run(query.result).map(rows => rows.map{ case (id, name) => (id.toString, name)})
    )
  }*/

  class CarsTable(tag: Tag) extends Table[CarsData](tag, "cars") {

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

    def garage = foreignKey("garages_fk", garagesId, Cars)(_.id, onDelete = ForeignKeyAction.Cascade)

    def * = (registration, brand, model, color, date_commissioning, price, garagesId) <> (CarsData.tupled, CarsData.unapply)
    def ? = (registration.?, brand.?, model.?, color.?, date_commissioning.?, price.?, garagesId.?).shaped.<>({ r => import r._; _1.map(_ => CarsData.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }
}