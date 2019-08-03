package DAO

import java.util.UUID
import akka.http.scaladsl.model.DateTime

case class Cars(id: UUID,
                registration: String,
                brand: String,
                model: String,
                color: String,
                date_commissioning: DateTime,
                price: Float)

