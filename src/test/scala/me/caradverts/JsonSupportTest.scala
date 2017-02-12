package me.caradverts

import me.caradverts.model.CarAdvert
import me.caradverts.json.{DateParser, JsonSupport}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class JsonSupportTest extends WordSpec with Matchers {

  "Json extension" should {
    "serialize and deserialize domain objects" in new JsonSupport {

      val date = DateParser.parseDate("2010-10-10")

      val advert = randomAdvert().copy(isNew = false, mileage = Some(10000), firstRegistration = Some(date))
      val json = advert.toJson
      val deserialized = json.convertTo[CarAdvert]

      advert shouldBe deserialized
    }

    //    "deserialize json" in new JsonSupport {
    //      val advert = randomAdvert().copy(isNew = false, mileage = Some(10000), firstRegistration = Some(DateParser.parseDate("2010-10-10")))

    //      val json = JsObject(
    //        "id" -> advert.id,
    //        "title" -> advert.title,
    //        "price" -> advert.price,
    //        "fuel" -> advert.fuel.toString,
    //        "isNew" -> JsFalse,
    //        "mileage" -> advert.mileage.get,
    //        "firstRegistration" -> DateParser.formatDate(advert.firstRegistration.get)
    //      ).toString

    //      val date = DateParser.parseDate("2010-10-10")
    //      val json = advert.toJson
    //      val deserialized = json.convertTo[CarAdvert]

    //      advert shouldBe deserialized
    //    }
  }
}
