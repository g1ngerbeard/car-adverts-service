package me.caradverts.service

import me.caradverts.model._

import scala.collection.concurrent.TrieMap

// todo: async interface?
trait CarAdvertService {

  def addOrModify(carAdvert: CarAdvert): Option[CarAdvert]

  def find(id: Int): Option[CarAdvert]

  def findAll(sortBy: Option[String]): List[CarAdvert] = {
    val list = findAllUnsorted()

    val fieldName = sortBy getOrElse "id"

    fieldName match {
      case "id" => list.sortBy(_.id)
      case "price" => list.sortBy(_.price)
      case "title" => list.sortBy(_.title)
      case "fuel" => list.sortBy(_.fuel)
      case "isNew" => list.sortBy(_.isNew)
      case "mileage" => list.sortBy(_.mileage)
      case "firstRegistration" => list.sortBy(_.firstRegistration)
      case _ => throw new IllegalArgumentException("Invalid field name")
    }
  }

  def findAllUnsorted(): List[CarAdvert]

  def delete(id: Int): Option[CarAdvert]

}

class InMemCarAdvertService extends CarAdvertService {

  val storage: TrieMap[Int, CarAdvert] = TrieMap()

  override def addOrModify(carAdvert: CarAdvert): Option[CarAdvert] = storage.put(carAdvert.id, carAdvert)

  override def findAllUnsorted(): List[CarAdvert] = storage.values.toList

  override def delete(id: Int): Option[CarAdvert] = storage.remove(id)

  override def find(id: Int): Option[CarAdvert] = storage.get(id)
}
