package me.caradverts

import akka.http.scaladsl.testkit.ScalatestRouteTest
import me.caradverts.service.InMemCarAdvertService
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class CarAdvertsServiceTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  trait TestContext {
    val service = new InMemCarAdvertService
    val carAdvert = randomAdvert()
    val id = carAdvert.id
  }

  "CarAdvertsService" should {
    "save advertisement" in new TestContext {
      whenReady(service.addOrModify(carAdvert))(_ shouldBe false)
      whenReady(service.find(id))(_ shouldBe Some(carAdvert))
    }

    "update advertisement" in new TestContext {
      whenReady(service.addOrModify(carAdvert))(_ shouldBe None)
      val newValue = randomAdvert().copy(id = id)

      whenReady(service.addOrModify(newValue))(_ shouldBe true)
      whenReady(service.find(id))(_ shouldBe Some(newValue))
    }

    "delete advertisement" in new TestContext {
      whenReady(service.addOrModify(carAdvert))(_ shouldBe false)
      whenReady(service.delete(id))(_ shouldBe true)
      whenReady(service.find(id))(_ shouldBe None)
    }

    "list all advertisements" in new TestContext {
      val testValues = randomAdverts(5)

      whenReady(Future.sequence(testValues.map(service.addOrModify))) { bools =>
        whenReady(service.findAll(None)) { result =>
          result should contain allElementsOf testValues
        }
      }
    }
  }
}
