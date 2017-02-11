package me.caradverts.json

import spray.json._

trait EnumJsonProtocol {

  def enumFormat[T <: Enumeration](enumeration: T) = new JsonFormat[enumeration.Value] {

    def write(obj: enumeration.Value) = JsString(obj.toString)

    def read(value: JsValue) = value match {
      case JsString(name) => enumeration.withName(name)
      case _ => deserializationError("String with enum value expected")
    }
  }
}
