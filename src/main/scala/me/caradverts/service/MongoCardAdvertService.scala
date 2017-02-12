package me.caradverts.service

import me.caradverts.config.MongoStorageConfig
import me.caradverts.json.JsonSupport
import me.caradverts.model.CarAdvert
import org.mongodb.scala.MongoClient
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

class MongoCardAdvertService(cfg: MongoStorageConfig)
                            (implicit executionContext: ExecutionContext)
  extends CarAdvertService with JsonSupport {

  val advertsCollection = MongoClient(s"mongodb://${cfg.host}:${cfg.port}")
    .getDatabase(cfg.databaseName)
    .getCollection("adverts")

  def create(carAdvert: CarAdvert): Future[CarAdvert] = {
    advertsCollection
      .insertOne(cardAdvert2document(carAdvert))
      .toFuture()
      .map(seq => carAdvert)
  }

  def update(carAdvert: CarAdvert): Future[Option[CarAdvert]] = {
    advertsCollection
      .replaceOne(Filters.eq("_id", carAdvert.id), cardAdvert2document(carAdvert))
      .toFuture()
      .map{
        case Seq(result) if result.getMatchedCount > 0 => Some(carAdvert)
        case _ => None
      }
  }

  override def findAllUnsorted(): Future[List[CarAdvert]] = {
    advertsCollection
      .find()
      .toFuture()
      .map(_.map(document2CardAdvert).toList)
  }

  override def find(id: Int): Future[Option[CarAdvert]] = {
    advertsCollection
      .find(Filters.eq("_id", id))
      .toFuture()
      .map(_.headOption.map(document2CardAdvert))
  }

  override def delete(id: Int): Future[Int] = {
    advertsCollection
      .deleteOne(Filters.eq("_id", id))
      .toFuture()
      .map(_.head.getDeletedCount.toInt)
  }

  private def document2CardAdvert(document: Document): CarAdvert = {
    document.toJson().parseJson.convertTo[CarAdvert]
  }

  private def cardAdvert2document(carAdvert: CarAdvert): Document = {
    Document(carAdvert.toJson.toString) + ("_id" -> carAdvert.id)
  }
}
