package Model

import java.util.Date

case class Garages(id: Int,
                   name: String,
                   address: String,
                   creation_date: Date,
                   max_cars_capacity: Int)

case class GaragesData(name: String,
                       address: String,
                       creation_date: Date,
                       max_cars_capacity: Int)
