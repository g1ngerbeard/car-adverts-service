package me.caradverts.model

import java.time.Instant

import me.caradverts.model.FuelType.FuelType

case class CarAdvert(id: Int,
                     title: String,
                     fuel: FuelType,
                     price: Int,
                     isNew: Boolean = true,
                     mileage: Option[Int] = None,
                     firstRegistration: Option[Instant] = None) {
  require(title.nonEmpty, "Title is empty")
  if (!isNew) {
    require(mileage.isDefined, "Mileage is not defined")
    require(firstRegistration.isDefined, "First registration is not defined")
  }

}

object FuelType extends Enumeration {
  type FuelType = Value
  val gasoline, diesel = Value
}

