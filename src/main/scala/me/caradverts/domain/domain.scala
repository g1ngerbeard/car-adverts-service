package me.caradverts.domain

import java.util.Date

import me.caradverts.domain.domain.FuelType.FuelType

object domain {

  case class CarAdvert(id: Int,
                       title: String,
                       fuel: FuelType,
                       price: Int,
                       isNew: Boolean = true,
                       mileage: Option[Int] = None,
                       // todo: replace with Instant
                       firstRegistration: Option[Date] = None) {
    require(title.nonEmpty, "Title is empty")
    if (!isNew) {
      require(mileage.nonEmpty, "Mileage is not defined")
      require(firstRegistration.nonEmpty, "First registration is not defined")
    }
  }

  object FuelType extends Enumeration {
    type FuelType = Value
    val gasoline, diesel = Value
  }

}
