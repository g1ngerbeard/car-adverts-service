package me.caradverts

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.MalformedRequestContentRejection
import me.caradverts.common.DefaultSpec
import me.caradverts.model.CarAdvert
import me.caradverts.rest.CarAdvertsRoute
import me.caradverts.service.InMemCarAdvertService
import org.scalatest.enablers.Sortable
import spray.json._

import scala.util.Random

class CarAdvertsRouteTest extends DefaultSpec {

  trait TestContext {
    val service = new InMemCarAdvertService
    val route = new CarAdvertsRoute(service, "*").route

    val existingAdverts = onboardAdverts(3)

    def onboardAdverts(number: Int): List[CarAdvert] = {
      val adverts = randomAdverts(number)
      adverts.foreach(service.create)
      adverts
    }
  }

  "Car advertisements REST service" should {
    "create advert" in new TestContext {
      val advert = randomAdvert()
      service.find(advert.id).futureValue shouldBe None
      val body = advert.toJson.toString
      val requestEntity = HttpEntity(MediaTypes.`application/json`, body)

      Post("/adverts", requestEntity) ~> route ~> check {
        service.find(advert.id).futureValue shouldBe defined
      }
    }

    "fail to create advert with existing id" in new TestContext {
      val advert = existingAdverts(1)
      val body = advert.toJson.toString
      val requestEntity = HttpEntity(MediaTypes.`application/json`, body)

      Post("/adverts", requestEntity) ~> route ~> check {
        status shouldBe Conflict
      }
    }

    "update advert" in new TestContext {
      val existingId = existingAdverts(2).id
      val newAdvert = randomAdvert().copy(id = existingId)

      val requestEntity = HttpEntity(MediaTypes.`application/json`, newAdvert.toJson.toString)

      Put("/adverts", requestEntity) ~> route ~> check {
        whenReady(service.find(existingId))(_ shouldBe Some(newAdvert))
      }
    }

    "return 404 when updating non-existing advert" in new TestContext {
      val newAdvert = randomAdvert()

      val requestEntity = HttpEntity(MediaTypes.`application/json`, newAdvert.toJson.toString)

      Put("/adverts", requestEntity) ~> route ~> check {
        status shouldBe NotFound
      }
    }

    "return existing advert" in new TestContext {
      val advert = existingAdverts(1)

      Get(s"/adverts/${advert.id}") ~> route ~> check {
        responseAs[CarAdvert] shouldBe advert
      }
    }

    "return 404 for non-existing advert" in new TestContext {
      val randomId = Random.nextInt(Int.MaxValue)

      Get(s"/adverts/$randomId") ~> route ~> check {
        status shouldBe NotFound
      }
    }

    "delete advert" in new TestContext {
      val existindId = existingAdverts(2).id

      Delete(s"/adverts/$existindId") ~> route ~> check {
        service.find(existindId).futureValue shouldBe None
      }
    }

    "return 404 when deleting non-existing advert" in new TestContext {
      val randomId = Random.nextInt(Int.MaxValue)

      Delete(s"/adverts/$randomId") ~> route ~> check {
        status shouldBe NotFound
      }
    }

    "list all existing adverts" in new TestContext {
      Get(s"/adverts") ~> route ~> check {
        responseAs[List[CarAdvert]] should have size 3
      }
    }

    "list adverts sorted by id" in sortingContext("id", _.id)

    "list adverts sorted by price" in sortingContext("price", _.price)

    "list adverts sorted by title" in sortingContext("title", _.title)

    "list adverts sorted by mileage" in sortingContext("mileage", _.mileage)

    "list adverts sorted by fuel" in sortingContext("fuel", _.fuel)

    "list adverts sorted by firstRegistration" in sortingContext("firstRegistration", _.firstRegistration)

    "fail on missing data" in new TestContext {
      val requiredFields = List("id", "title", "price", "fuel", "new")

      requiredFields.foreach { fieldName =>
        val body = JsObject(carAdvertFields - fieldName).toString
        val requestEntity = HttpEntity(MediaTypes.`application/json`, body)

        Post("/adverts", requestEntity) ~> route ~> check {
          rejection match {
            case MalformedRequestContentRejection(message, _) => message.contains("Object is missing required member")
          }
        }
      }
    }

    "fail on invalid data" in new TestContext {
      val invalidFields = List("id", "price", "fuel", "new", "mileage", "firstRegistration")

      invalidFields.foreach { fieldName =>
          val body = JsObject(carAdvertFields + (fieldName -> JsString("invalid"))).toString
          val requestEntity = HttpEntity(MediaTypes.`application/json`, body)

          Post("/adverts", requestEntity) ~> route ~> check {
            assert(rejection.isInstanceOf[MalformedRequestContentRejection])
          }
      }
    }
  }

  val carAdvertFields: Map[String, JsValue] = {
    val json =
      s"""
         |{
         |"id": 1,
         |"title": "volvo",
         |"price": 100000,
         |"fuel": "gasoline",
         |"new": false,
         |"mileage": 100000,
         |"firstRegistration": "1999-07-22"
         |}
      """.stripMargin
    json.parseJson.asJsObject.fields
  }

  def sortingContext[T](fieldName: String, mapper: CarAdvert => T)(implicit sortable: Sortable[List[T]]) = {
    new TestContext {
      Get(s"/adverts?sortBy=$fieldName") ~> route ~> check {
        responseAs[List[CarAdvert]].map(mapper) shouldBe sorted
      }
    }
  }
}
