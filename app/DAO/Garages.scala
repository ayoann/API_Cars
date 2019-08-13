package DAO

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{JdbcProfile, TransactionIsolation}

import scala.concurrent.{ExecutionContext, Future}

case class GaragesData(id: Int, name: String, adress: String, creation_date: Date, max_cars_capacity: Int)

@Singleton
class GaragesRepo @Inject()(carsRepo: CarsRepo)(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  val Garages = TableQuery[GaragesTable]

  private def _findById(id: Int): DBIO[Option[GaragesData]] =
    Garages.filter(_.id === id).result.headOption

  def findById(id: Int): Future[Option[GaragesData]] =
    db.run(_findById(id))

  def create(garages: GaragesData): Future[String] = {
    db.run(Garages.returning(Garages.map(_.name)) += garages)
  }

  def update(garages: GaragesData): Future[Option[GaragesData]] = {
    val query = Garages.filter(_.id === garages.id)
    val action = for {
      results        <- query.result.headOption
      _   <- query.update(garages)
    } yield results
    db.run(action.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }

  /*def deleteById(id: Int): Future[Option[GaragesData]] = {
    val query = Garages.filter(_.id === id)
    val interaction = for {
      garages <- query.result.headOption
      _       <- DBIO.sequence(garages.map(g => carsRepo._deleteAllInGarages(g.id)))
      _ <- query.delete
    } yield garages
    db.run(interaction.withTransactionIsolation(TransactionIsolation.RepeatableRead))
  }*/

  class GaragesTable(tag: Tag) extends Table[GaragesData](tag, "GARAGES") {

    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    def id = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    def name = column[String]("NAME")
    def adress = column[String]("ADDRESS")
    def creation_date = column[Date]("CREATION_DATE")
    def max_cars_capacity = column[Int]("MAX_CARS_CAPACITY")

    def * = (id, name, adress, creation_date, max_cars_capacity) <> (GaragesData.tupled, GaragesData.unapply)

    def ? = (id.?, name.?, adress.?, creation_date.?, max_cars_capacity.?).shaped.<>({ r =>import r._; _1.map(_ => GaragesData.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

}
