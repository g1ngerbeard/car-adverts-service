package me.caradverts

import me.caradverts.common.{EmbeddedMongoSupport, DefaultSpec}
import me.caradverts.config.MongoStorageConfig
import me.caradverts.service.MongoCardAdvertService

import scala.concurrent.Future

class CarAdvertsServiceTest extends DefaultSpec with EmbeddedMongoSupport {

  trait TestContext {
    val service = new MongoCardAdvertService(MongoStorageConfig("localhost", 12345))

    val carAdvert = randomAdvert()
    val id = carAdvert.id
  }

  "CarAdvertsService" should {
    "create advertisement" in new TestContext {
      service.create(carAdvert).futureValue
      whenReady(service.find(id))(_ shouldBe Some(carAdvert))
    }

    "update advertisement" in new TestContext {
      service.create(carAdvert).futureValue

      val newValue = randomAdvert().copy(id = id)
      service.update(newValue).futureValue

      whenReady(service.find(id))(_ shouldBe Some(newValue))
    }

    "delete advertisement" in new TestContext {
      service.create(carAdvert).futureValue

      whenReady(service.delete(id))(_ shouldBe 1)
      whenReady(service.find(id))(_ shouldBe None)
    }

    "list all advertisements" in new TestContext {
      val testValues = randomAdverts(5)

      whenReady(Future.sequence(testValues.map(service.create))) { bools =>
        whenReady(service.findAll(None)) { result =>
          result should contain allElementsOf testValues
        }
      }
    }
  }
}
