package me.caradverts

import akka.http.scaladsl.model.StatusCodes.{NoContent, NotFound, OK}
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import me.caradverts.common.{DefaultSpec, EmbeddedMongoSupport}
import me.caradverts.config.MongoStorageConfig
import me.caradverts.model.CarAdvert
import me.caradverts.rest.CarAdvertsRoute
import me.caradverts.service.MongoCardAdvertService
import spray.json._

import scala.util.Random

class IntegrationTest extends DefaultSpec with EmbeddedMongoSupport {

  trait TestContext {
    val service = new MongoCardAdvertService(MongoStorageConfig("localhost", 12345))
    val route = new CarAdvertsRoute(service).route
  }

  "Car adverts service" should {
    "be exposed via REST endpoint and integrate with DB" in new TestContext {
      val advertsSize = 5
      val carAdverts = randomAdverts(advertsSize)

      carAdverts.foreach { advert =>
        val body = advert.toJson.toString
        val requestEntity = HttpEntity(MediaTypes.`application/json`, body)

        Post("/adverts", requestEntity) ~> route ~> check {
          response.status shouldBe OK
        }
      }

      val advert = carAdverts(Random.nextInt(advertsSize))
      val advertId = advert.id

      Get(s"/adverts/$advertId") ~> route ~> check {
        responseAs[CarAdvert] shouldBe advert
      }

      Delete(s"/adverts/$advertId") ~> route ~> check {
        response.status shouldBe NoContent
      }

      Get(s"/adverts/$advertId") ~> route ~> check {
        response.status shouldBe NotFound
      }

      Get(s"/adverts?sortBy=price") ~> route ~> check {
        val response = responseAs[List[CarAdvert]]

        print(response)
        response.size shouldBe (advertsSize - 1)
        response.map(_.price) shouldBe sorted
      }
    }
  }
}
