package me.caradverts.rest

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import me.caradverts.domain.domain.CarAdvert
import me.caradverts.json.JsonSupport
import me.caradverts.service.CarAdvertService

class CarAdvertsRoute(service: CarAdvertService) extends JsonSupport {

  val route = Directives.pathPrefix("adverts") {
    get {
      pathEndOrSingleSlash {
        complete(service.findAll())
      } ~
        pathPrefix(IntNumber) { id =>
          service.find(id) match {
            case Some(advert) => complete(advert)
            case _ => complete(NotFound)
          }
        }
    }
  } ~
    post {
      entity(as[CarAdvert]) { carAdvert =>
        service.addOrModify(carAdvert) match {
          case Some(_) => complete(OK)
          case _ => complete(Created)
        }
      }
    } ~
    delete {
      pathPrefix(IntNumber) { id =>
        service.delete(id) match {
          case Some(_) => complete(OK)
          case _ => complete(NotFound)
        }
      }
    }
}