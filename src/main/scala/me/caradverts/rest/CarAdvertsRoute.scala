package me.caradverts.rest

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import me.caradverts.json.JsonSupport
import me.caradverts.model.CarAdvert
import me.caradverts.service.CarAdvertService
import spray.json._

import scala.concurrent.ExecutionContext

class CarAdvertsRoute(service: CarAdvertService)(implicit executionContext: ExecutionContext)
  extends JsonSupport {

  val route =
    pathPrefix("adverts") {
      get {
        pathEndOrSingleSlash {
          parameter('sortBy.?) { p =>
            complete(service.findAll(p).map(_.toJson))
          }
        } ~
          pathPrefix(IntNumber) { id =>
            onSuccess(service.find(id)) {
              case Some(advert) => complete(advert.toJson)
              case _ => complete(NotFound)
            }
          }
      } ~
        post {
          entity(as[CarAdvert]) { carAdvert =>
            complete(service.addOrModify(carAdvert).map(if (_) OK else Created))
          }
        } ~
        delete {
          pathPrefix(IntNumber) { id =>
            complete(service.delete(id).map(if (_) OK else NotFound))
          }
        }
    }
}