package me.caradverts.service

import me.caradverts.model._

import scala.concurrent.{ExecutionContext, Future}

trait CarAdvertService {

  def create(carAdvert: CarAdvert): Future[CarAdvert]

  def update(carAdvert: CarAdvert): Future[CarAdvert]

  def find(id: Int): Future[Option[CarAdvert]]

  def findAll(sortBy: Option[String])
             (implicit executionContext: ExecutionContext): Future[List[CarAdvert]] = {
    findAllUnsorted().map(list => {
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
    })
  }

  def findAllUnsorted(): Future[List[CarAdvert]]

  def delete(id: Int): Future[Int]

}
