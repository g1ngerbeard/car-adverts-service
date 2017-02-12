package me.caradverts.service

import org.mongodb.scala.model.UpdateOptions
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
    .getDatabase("db")
    .getCollection("adverts")

  override def addOrModify(carAdvert: CarAdvert): Future[Boolean] = {
    val document = Document(carAdvert.toJson.toString) + ("_id" -> carAdvert.id)

    advertsCollection.updateOne(
      Filters.eq("_id", carAdvert.id),
      document,
      UpdateOptions().upsert(true)
    ).toFuture().map(_.nonEmpty)
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

  override def delete(id: Int): Future[Boolean] = {
    advertsCollection
      .deleteOne(Filters.eq("_id", id))
      .toFuture()
      .map(_.headOption.exists(result => result.getDeletedCount > 0))
  }

  private def document2CardAdvert(document: Document): CarAdvert = {
    document.toJson().parseJson.convertTo[CarAdvert]
  }
}
