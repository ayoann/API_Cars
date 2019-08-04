package DAO

import java.util.Date

case class Cars(id: Int,
                registration: String,
                brand: String,
                model: String,
                color: String,
                date_commissioning: Date,
                price: Float)

