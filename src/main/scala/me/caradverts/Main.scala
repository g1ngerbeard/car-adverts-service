package me.caradverts

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import me.caradverts.config.{MongoStorageConfig, InMemStorageConfig, AppConfig}
import me.caradverts.rest.CarAdvertsRoute
import me.caradverts.service.{MongoCardAdvertService, InMemCarAdvertService}

object Main extends App {

  implicit val actorSystem = ActorSystem("adverts-system")
  implicit val executor = actorSystem.dispatcher
  implicit val actorMaterializer = ActorMaterializer()
  implicit val log = Logging(actorSystem, getClass)

  val config = new AppConfig(ConfigFactory.load())

  val service = config.db match {
    case InMemStorageConfig => new InMemCarAdvertService
    case cfg: MongoStorageConfig => new MongoCardAdvertService(cfg)
  }

  val carAdvertsRoute = new CarAdvertsRoute(service)

  Http().bindAndHandle(carAdvertsRoute.route, config.http.host, config.http.port)

}
