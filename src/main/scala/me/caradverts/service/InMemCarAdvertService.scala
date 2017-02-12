package me.caradverts.service

import me.caradverts.model.CarAdvert

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

class InMemCarAdvertService extends CarAdvertService {

  val storage: TrieMap[Int, CarAdvert] = TrieMap()

  override def create(carAdvert: CarAdvert): Future[CarAdvert] = {
    if (storage.contains(carAdvert.id)) {
      Future.failed(new IllegalArgumentException("Already exists"))
    } else {
      storage += (carAdvert.id -> carAdvert)
      Future.successful(carAdvert)
    }
  }

  override def update(carAdvert: CarAdvert): Future[CarAdvert] = {
    if (storage.contains(carAdvert.id)) {
      storage += (carAdvert.id -> carAdvert)
      Future.successful(carAdvert)
    } else {
      Future.failed(new IllegalArgumentException("Not found"))
    }
  }

  override def findAllUnsorted(): Future[List[CarAdvert]] = Future.successful(storage.values.toList)

  override def find(id: Int): Future[Option[CarAdvert]] = Future.successful(storage.get(id))

  override def delete(id: Int): Future[Int] = {
    storage.remove(id)
    Future.successful(1)
  }

}
