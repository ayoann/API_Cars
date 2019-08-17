package Model

import java.util.Date

case class Cars(id: Int,
                registration: String,
                brand: String,
                model: String,
                color: String,
                date_commissioning: Date,
                price: Float,
                garagesId: Int)


case class CarsData(registration: String,
                    brand: String,
                    model: String,
                    color: String,
                    date_commissioning: Date,
                    price: Float,
                    garagesId: Int)
