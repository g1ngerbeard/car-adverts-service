package me.caradverts

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import me.caradverts.domain.domain.CarAdvert
import me.caradverts.json.JsonSupport
import me.caradverts.rest.CarAdvertsRoute
import me.caradverts.service.InMemCarAdvertService
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class CarAdvertsRouteTest extends WordSpec with Matchers with ScalatestRouteTest {

  trait TestContext extends JsonSupport {
    val service = new InMemCarAdvertService
    val route = new CarAdvertsRoute(service).route

    val existingAdverts = onboardAdverts(3)

    def onboardAdverts(number: Int): List[CarAdvert] = {
      val adverts = randomAdverts(number)
      adverts.foreach(service.addOrModify)
      adverts
    }
  }

  "Car advertisements REST service" should {
    "create advert" in new TestContext {
      val advert = randomAdvert()
      val body = advert2Json(advert)

      val requestEntity = HttpEntity(MediaTypes.`application/json`, body)

      Post("/adverts", requestEntity) ~> route ~> check {
        response.status shouldBe Created
        service.find(advert.id) shouldBe defined
      }
    }

    "update advert" in new TestContext {
      val existingId = existingAdverts(3).id
      val newAdvert = randomAdvert().copy(id = existingId)

      val requestEntity = HttpEntity(MediaTypes.`application/json`, advert2Json(newAdvert))

      Post("/adverts", requestEntity) ~> route ~> check {
        response.status shouldBe OK
        service.find(existingId) shouldBe newAdvert
      }
    }

    "return existing advert" in new TestContext {
      val advert = existingAdverts(1)

      Get(s"/adverts/${advert.id}") ~> route ~> check {
        responseAs[CarAdvert] shouldBe advert
      }
    }

    "return 404 for non-existing advert" in new TestContext {
      val randomId = Random.nextInt()

      Get(s"/adverts/$randomId") ~> route ~> check {
        response.status shouldBe NotFound
      }
    }

    "delete advert" in new TestContext {
      val existindId = existingAdverts(2).id

      Delete(s"/adverts/$existindId") ~> route ~> check {
        response.status shouldBe OK
      }
    }

    "list all existing adverts" in new TestContext {
      Get(s"/adverts") ~> route ~> check {
        responseAs[List[CarAdvert]] should have size 3
      }
    }
  }

  //  todo: car adverts rest service should:

  //  fail on incorrect data (id, title, fuel, price, isNew, mileage, registration)
  //  fail on missing data (id, title, fuel, price, isNew, old car without mileage and registration)

  //  order list by (id, title, fuel, price)

  //  handle CORS

}
