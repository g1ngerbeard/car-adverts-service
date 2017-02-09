package me.caradverts.service

import scala.collection.concurrent.TrieMap

import me.caradverts.domain.domain._

// todo: async interface?
trait CarAdvertService {

  def addOrModify(carAdvert: CarAdvert): Unit

  def find(id: Int): Option[CarAdvert]

  // todo: add sorting criteria
  def findAll(): List[CarAdvert]

  def delete(id: Int): Unit

}

class InMemCarAdvertService extends CarAdvertService {

  val storage: TrieMap[Int, CarAdvert] = TrieMap()

  override def addOrModify(carAdvert: CarAdvert): Unit = storage += (carAdvert.id -> carAdvert)

  override def findAll(): List[CarAdvert] = storage.values.toList

  override def delete(id: Int): Unit = storage -= id

  override def find(id: Int): Option[CarAdvert] = storage.get(id)
}
