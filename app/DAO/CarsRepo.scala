package DAO

import java.util.Date

import Model.Cars
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{JdbcProfile, TransactionIsolation}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CarsRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  val CarsTable = TableQuery[CarsTable]


  private def _findById(id: Int): DBIO[Option[Cars]] =
    CarsTable.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[Cars]] =
    db.run(_findById(id))

  def findByColor(color: String, garagesId: Int): Future[List[Cars]] = {
    db.run(CarsTable.filter(_.garagesId === garagesId).to[List].filter(_.color === color).to[List].result)
  }

  def findByPrice(garagesId: Int, min: Float, max: Float): Future[List[Cars]] = {
    db.run(CarsTable.filter(_.garagesId === garagesId).to[List].filter( c => c.price >= min && c.price <= max).to[List].result)
  }


  def all(garagesId: Int): Future[List[Cars]] =
    db.run(CarsTable.filter(_.garagesId === garagesId).to[List].result)

  class CarsTable(tag: Tag) extends Table[Cars](tag, "cars") {

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

    def garage = foreignKey("garages_fk", garagesId, CarsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def * = (id, registration, brand, model, color, date_commissioning, price, garagesId) <> (Cars.tupled, Cars.unapply)
    def ? = (id.?, registration.?, brand.?, model.?, color.?, date_commissioning.?, price.?, garagesId.?).shaped.<>({ r => import r._; _1.map(_ => Cars.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }
}
