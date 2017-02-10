package me.caradverts.json

import java.text.SimpleDateFormat
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.caradverts.domain.domain.FuelType.FuelType
import me.caradverts.domain.domain.{CarAdvert, FuelType}
import spray.json._

object JsonSupport {
  // todo: move somewhere else :)
  val simpleDateFormat = new ThreadLocal[SimpleDateFormat] {
    override def initialValue(): SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  }
}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  import JsonSupport._

  implicit val dateFormat = new JsonFormat[Date] {

    override def write(obj: Date): JsValue = JsString(simpleDateFormat.get().format(obj))

    override def read(json: JsValue): Date = {
      json match {
        case JsString(value) => simpleDateFormat.get().parse(value)
        case _ => deserializationError("Invalid date format")
      }
    }
  }

  implicit val fuelFormat = new JsonFormat[FuelType] {

    def write(obj: FuelType) = JsString(obj.toString)

    def read(value: JsValue) = value match {
      case JsString(name) => FuelType.withName(name)
      case _ => deserializationError("String with fuel type expected")
    }
  }

  implicit val carAdvertFormat = jsonFormat7(CarAdvert)

}
