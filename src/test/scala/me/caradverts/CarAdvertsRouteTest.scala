package me.caradverts

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import me.caradverts.rest.CarAdvertsRoute
import me.caradverts.service.InMemCarAdvertService
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class CarAdvertsRouteTest extends WordSpec with Matchers with ScalatestRouteTest {

  trait TestContext {
    val service = new InMemCarAdvertService
    val route = new CarAdvertsRoute(service).route
  }

  "Car advertisements REST service" should {
    "create advert" in new TestContext {
      val id = Random.nextInt(Int.MaxValue)

      val request =
        s"""
           |{
           |"id": $id,
           |"title": "volvo",
           |"price": 10000,
           |"fuel": "gasoline",
           |"isNew": false,
           |"mileage": 20000,
           |"firstRegistration": "1992-07-24"
           |}
           |""".stripMargin

      val requestEntity = HttpEntity(MediaTypes.`application/json`, request)

      Post("/adverts", requestEntity) ~> route ~> check {
        response.status should be(Created)
        service.find(id) shouldBe defined
      }
    }
  }

  //  todo: car adverts rest service should:

  //  update advert (OK)
  //  return existing advert
  //  return 404 for non-existing advert

  //  fail on incorrect data (id, title, fuel, price, isNew, mileage, registration)
  //  fail on missing data (id, title, fuel, price, isNew, old car without mileage and registration)

  //  list all existing adverts
  //  order list by (id, title, fuel, price)

  //  handle CORS

}
