package me.caradverts

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import me.caradverts.common.DefaultSpec
import me.caradverts.json.JsonSupport
import me.caradverts.model.CarAdvert
import me.caradverts.rest.CarAdvertsRoute
import me.caradverts.service.InMemCarAdvertService
import org.scalatest.enablers.Sortable
import spray.json._

import scala.util.Random

class CarAdvertsRouteTest extends DefaultSpec {

  trait TestContext extends JsonSupport {
    val service = new InMemCarAdvertService
    val route = new CarAdvertsRoute(service).route

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

    "update advert" in new TestContext {
      val existingId = existingAdverts(2).id
      val newAdvert = randomAdvert().copy(id = existingId)

      val requestEntity = HttpEntity(MediaTypes.`application/json`, newAdvert.toJson.toString)

      Put("/adverts", requestEntity) ~> route ~> check {
        whenReady(service.find(existingId))(_ shouldBe Some(newAdvert))
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
        response.status shouldBe NotFound
      }
    }

    "delete advert" in new TestContext {
      val existindId = existingAdverts(2).id

      Delete(s"/adverts/$existindId") ~> route ~> check {
        service.find(existindId).futureValue shouldBe None
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

  }

  def sortingContext[T](fieldName: String, mapper: CarAdvert => T)(implicit sortable: Sortable[List[T]]) = {
    new TestContext {
      Get(s"/adverts?sortBy=$fieldName") ~> route ~> check {
        responseAs[List[CarAdvert]].map(mapper) shouldBe sorted
      }
    }
  }

  //  todo: car adverts rest service should:

  //  fail on incorrect data (id, title, fuel, price, isNew, mileage, registration)
  //  fail on missing data (id, title, fuel, price, isNew, old car without mileage and registration)

  //  handle CORS

}
