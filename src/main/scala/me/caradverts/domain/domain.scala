package me.caradverts.domain

import java.time.Instant

object domain {

  case class CarAdvert(id: Int,
                       title: String,
                       fuel: FuelType,
                       price: Int,
                       isNew: Boolean = true,
                       mileage: Option[Int] = None,
                       firstRegistration: Option[Instant] = None) {

    require(title.nonEmpty, "Title is empty")
    if (!isNew) {
      require(mileage.nonEmpty, "Mileage is not defined")
      require(firstRegistration.nonEmpty, "First registration is not defined")
    }
  }

  sealed trait FuelType

  case object Gasoline extends FuelType

  case object Diesel extends FuelType

}
