package me.caradverts.common

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

trait DefaultSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

}
