package me.caradverts.service

import scala.collection.concurrent.TrieMap

import me.caradverts.domain.domain._

// todo: async interface?
trait CarAdvertService {

  // todo: partial update
  def addOrModify(carAdvert: CarAdvert): Option[CarAdvert]

  def find(id: Int): Option[CarAdvert]

  // todo: add sorting criteria
  def findAll(): List[CarAdvert]

  def delete(id: Int): Option[CarAdvert]

}

class InMemCarAdvertService extends CarAdvertService {

  val storage: TrieMap[Int, CarAdvert] = TrieMap()

  override def addOrModify(carAdvert: CarAdvert): Option[CarAdvert] = storage.put(carAdvert.id, carAdvert)

  override def findAll(): List[CarAdvert] = storage.values.toList

  override def delete(id: Int): Option[CarAdvert] = storage.remove(id)

  override def find(id: Int): Option[CarAdvert] = storage.get(id)
}
