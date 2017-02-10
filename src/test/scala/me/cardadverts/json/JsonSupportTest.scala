package me.cardadverts.json

import java.text.SimpleDateFormat

import me.caradverts.domain.domain.{CarAdvert, FuelType}
import me.caradverts.json.JsonSupport
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class JsonSupportTest extends WordSpec with Matchers {

  "Json extension" should {
    "serialize domain objects" in new JsonSupport {

      val date = new SimpleDateFormat("yyyy-MM-dd").parse("2010-10-10")

      val advert = CarAdvert(1, "volvo", FuelType.gasoline, 1000, isNew = false, Some(10000), Some(date))

      val json = advert.toJson
      val deserialized = json.convertTo[CarAdvert]

      advert shouldBe deserialized
    }
  }
}
