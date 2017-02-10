package me

import java.util.Date

import me.caradverts.domain.domain.{CarAdvert, FuelType}

import scala.util.Random

package object caradverts {

  def randomAdvert(): CarAdvert = {
    val fuelType = FuelType.values.toList(Random.nextInt(FuelType.values.size))
    val advert = CarAdvert(Random.nextInt(Int.MaxValue), Random.nextString(10), fuelType, Random.nextInt(10000))

    if (Random.nextBoolean()) {
      // val date = new Date(LocalDate.of(1970, 1, 1).plus(Random.nextInt(17000), DAYS).toEpochDay)
      advert.copy(isNew = false, mileage = Some(Random.nextInt(10000)), firstRegistration = Some(new Date))
    } else {
      advert
    }
  }

  def randomAdverts(n: Int): List[CarAdvert] = {
    (1 to n).map(i => randomAdvert()).toList
  }

//  def randomAdvertJson(advert: CarAdvert): String = {
//    s"""
//       |{
//       |"id": ${advert.id},
//       |"title": "${advert.title}",
//       |"price": ${advert.price},
//       |"fuel": "${advert.fuel}",
//       |"isNew": false,
//       |"mileage": ${Random.nextInt(100000)},
//       |"firstRegistration": "1992-07-24"
//       |}
//       |""".stripMargin
//  }
}
