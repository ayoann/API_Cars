package DAO

import java.util.Date

import Model.GaragesData
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{JdbcProfile, TransactionIsolation}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class GaragesDataRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  val Garages = TableQuery[GaragesTable]

  private def _findById(id: Int): DBIO[Option[GaragesData]] =
    Garages.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[GaragesData]] =
    db.run(_findById(id))

  def create(garages: GaragesData): Future[Int] = {
    db.run(Garages.returning(Garages.map(_.id)) += garages)
  }

  def update(garages: GaragesData, id: String): Future[Option[GaragesData]] = {
    val query = Garages.filter(_.id === id.toInt)
    val action = for {
      results        <- query.result.headOption
      _              <- query.update(garages)
    } yield results
    db.run(action.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }

  def deleteById(id: Int): Future[Int] = {
    db.run(Garages.filter(_.id === id).delete)
  }

  class GaragesTable(tag: Tag) extends Table[GaragesData](tag, "garages") {

    implicit val DateTimeColumeType =  MappedColumnType.base[java.util.Date,java.sql.Timestamp](
      { d => java.sql.Timestamp.from( d.toInstant) },
      { t => Date.from(t.toInstant) }
    )

    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")
    def address = column[String]("address")
    def creation_date = column[Date]("creation_date")
    def max_cars_capacity = column[Int]("max_cars_capacity")

    def * = (name, address, creation_date, max_cars_capacity) <> (GaragesData.tupled, GaragesData.unapply)

    def ? = (name.?, address.?, creation_date.?, max_cars_capacity.?).shaped.<>({ r =>import r._; _1.map(_ => GaragesData.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

}
