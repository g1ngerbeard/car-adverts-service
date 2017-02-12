package me.caradverts

import me.caradverts.model.CarAdvert
import me.caradverts.json.{DateParser, JsonSupport}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

import scala.util.Random

class JsonSupportTest extends WordSpec with Matchers {

  "Json extension" should {
    "serialize and deserialize domain objects" in new JsonSupport {

      val date = DateParser.parseDate("2010-10-10")

      val advert = randomAdvert().copy(`new` = false, mileage = Some(10000), firstRegistration = Some(date))
      val json = advert.toJson
      val deserialized = json.convertTo[CarAdvert]

      advert shouldBe deserialized
    }

    "deserialize json" in new JsonSupport {
      val advert = randomAdvert().copy(
        `new` = false,
        mileage = Some(Random.nextInt(200000)),
        firstRegistration = Some(DateParser.parseDate("1999-07-22"))
      )

      val json =
        s"""
           |{
           |"id": ${advert.id},
           |"title": "${advert.title}",
           |"price": ${advert.price},
           |"fuel": "${advert.fuel.toString}",
           |"new": false,
           |"mileage": ${advert.mileage.get},
           |"firstRegistration": "1999-07-22"
           |}
      """.stripMargin

      val deserialized = json.parseJson.convertTo[CarAdvert]

      advert shouldBe deserialized
    }
  }
}
