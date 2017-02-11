package me.caradverts.json

import java.time.ZoneId._
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, ZoneOffset}
import java.util.Locale._

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.caradverts.model._
import spray.json._

object DateParser {

  //  todo: make it an implicit convertion
  val formatter = DateTimeFormatter
    // todo: use JS date format
    .ofPattern("yyyy-MM-dd")
    .withLocale(ENGLISH)
    .withZone(systemDefault())

  def formatDate(instant: Instant): String = formatter.format(instant)

  def parseDate(value: String): Instant =
    LocalDate.parse(value, formatter)
      .atStartOfDay()
      .toInstant(ZoneOffset.UTC)
}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport with EnumJsonProtocol {

  import DateParser._

  implicit val dateFormat = new JsonFormat[Instant] {

    override def write(obj: Instant): JsValue = JsString(formatDate(obj))

    override def read(json: JsValue): Instant = {
      json match {
        case JsString(value) => parseDate(value)
        case _ => deserializationError("Invalid date format")
      }
    }
  }

  implicit val fuelFormat = enumFormat(FuelType)

  implicit val carAdvertFormat = jsonFormat7(CarAdvert)

}
