package DAO

import java.util.UUID
import akka.http.scaladsl.model.DateTime

case class Garages(id: UUID,
                   name: String,
                   adress: String,
                   creation_date: DateTime,
                   max_cars_capacity: Int)
