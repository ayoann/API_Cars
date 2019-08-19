package DAO

import java.util.Date

import Model.Garages
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GaragesRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  val GaragesTable = TableQuery[GaragesTable]

  private def _findById(id: Int): DBIO[Option[Garages]] =
    GaragesTable.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[Garages]] =
    db.run(_findById(id))

  class GaragesTable(tag: Tag) extends Table[Garages](tag, "garages") {

    implicit val DateTimeColumeType =  MappedColumnType.base[java.util.Date,java.sql.Timestamp](
      { d => java.sql.Timestamp.from( d.toInstant) },
      { t => Date.from(t.toInstant) }
    )

    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")
    def address = column[String]("address")
    def creation_date = column[Date]("creation_date")
    def max_cars_capacity = column[Int]("max_cars_capacity")

    def * = (id, name, address, creation_date, max_cars_capacity) <> (Garages.tupled, Garages.unapply)

    def ? = (id.?, name.?, address.?, creation_date.?, max_cars_capacity.?).shaped.<>({ r =>import r._; _1.map(_ => Garages.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

}
