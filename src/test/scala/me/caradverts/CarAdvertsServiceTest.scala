package me.caradverts

import me.caradverts.service.InMemCarAdvertService
import org.scalatest.{Matchers, WordSpec}

class CarAdvertsServiceTest extends WordSpec with Matchers {

  trait TestContext {
    val service = new InMemCarAdvertService
    val carAdvert = randomAdvert()
    val id = carAdvert.id
  }

  "CarAdvertsService" should {
    "save advertisement" in new TestContext {
      service.addOrModify(carAdvert) shouldBe None
      service.find(id) shouldBe Some(carAdvert)
    }

    "update advertisement" in new TestContext {
      service.addOrModify(carAdvert) shouldBe None
      val newValue = randomAdvert().copy(id = id)
      service.addOrModify(newValue) shouldBe defined
      service.find(id) shouldBe Some(newValue)
    }

    "delete advertisement" in new TestContext {
      service.addOrModify(carAdvert) shouldBe None
      service.delete(id) shouldBe defined
      service.find(id) shouldBe None
    }

    "list all advertisements" in new TestContext {
      val testValues = randomAdverts(5)
      testValues.foreach(service.addOrModify)
      service.findAll(None) should contain allElementsOf testValues
    }
  }
}
