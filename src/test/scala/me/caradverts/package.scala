package me

import java.time.Instant
import java.time.temporal.ChronoUnit

import me.caradverts.model.{CarAdvert, FuelType}
import spray.json._

import scala.util.Random

package object caradverts {

  def randomAdvert(): CarAdvert = {
    val fuelType = FuelType.values.toList(Random.nextInt(FuelType.values.size))
    val id = Random.nextInt(Int.MaxValue)
    val title = Random.nextString(10)
    val price = Random.nextInt(10000)

    val advert = CarAdvert(id, title, fuelType, price)

    if (Random.nextBoolean()) {
      val date = Instant.now().truncatedTo(ChronoUnit.DAYS)
      advert.copy(isNew = false, mileage = Some(Random.nextInt(10000)), firstRegistration = Some(date))
    } else {
      advert
    }
  }

  def randomAdverts(n: Int): List[CarAdvert] = {
    (1 to n).map(i => randomAdvert()).toList
  }

  def advert2Json(advert: CarAdvert): String = {
    import me.caradverts.json.DateParser._

    implicit def string2jsString(str: String): JsValue = JsString(str)

    implicit def int2jsNumber(i: Int): JsValue = JsNumber(i)

    if (advert.isNew) {
      JsObject(
        "id" -> advert.id,
        "title" -> advert.title,
        "price" -> advert.price,
        "fuel" -> advert.fuel.toString,
        "isNew" -> JsTrue
      ).toString
    } else {
      JsObject(
        "id" -> advert.id,
        "title" -> advert.title,
        "price" -> advert.price,
        "fuel" -> advert.fuel.toString,
        "isNew" -> JsFalse,
        "mileage" -> advert.mileage.get,
        "firstRegistration" -> formatDate(advert.firstRegistration.get)
      ).toString
    }
  }
}
