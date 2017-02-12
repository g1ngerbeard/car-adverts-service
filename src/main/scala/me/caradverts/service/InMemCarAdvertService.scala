package me.caradverts.service

import me.caradverts.model.CarAdvert

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

class InMemCarAdvertService extends CarAdvertService {

  val storage: TrieMap[Int, CarAdvert] = TrieMap()

  override def addOrModify(carAdvert: CarAdvert): Future[Boolean] = {
    Future.successful(storage.put(carAdvert.id, carAdvert).isDefined)
  }

  override def findAllUnsorted(): Future[List[CarAdvert]] = Future.successful(storage.values.toList)

  override def find(id: Int): Future[Option[CarAdvert]] = Future.successful(storage.get(id))

  override def delete(id: Int): Future[Boolean] = Future.successful(storage.remove(id).isDefined)
}
