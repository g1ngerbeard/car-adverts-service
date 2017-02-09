package me.caradverts

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import me.caradverts.config.AppConfig
import me.caradverts.service.InMemCarAdvertService

object Main extends App {

  implicit val actorSystem = ActorSystem("adverts-system")
  implicit val actorMaterializer = ActorMaterializer()
  implicit val log = Logging(actorSystem, getClass)

  val config = new AppConfig(ConfigFactory.load())

  val service = new InMemCarAdvertService

  // todo: json serialization
  val route = Directives.pathPrefix("adverts") {
    get {
      pathPrefix(IntNumber) { id =>
        service.find(id) match {
          case Some(advert) => complete(advert.title)
          case _ => complete(NotFound)
        }
      }
    }
    //    ~
    //      post {
    //        entity(as[CarAdvert]){ carAdvert =>
    //          service.addOrModify(carAdvert)
    //          complete(OK)
    //        }
    //    }
  }

  Http().bindAndHandle(route, config.http.host, config.http.port)

}
