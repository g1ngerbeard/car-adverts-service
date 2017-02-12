package me.caradverts.rest

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import ch.megard.akka.http.cors.{CorsDirectives, CorsSettings}
import me.caradverts.json.JsonSupport
import me.caradverts.model.CarAdvert
import me.caradverts.service.CarAdvertService
import spray.json._

import scala.concurrent.ExecutionContext

class CarAdvertsRoute(service: CarAdvertService, allowedOrigin: String)(implicit executionContext: ExecutionContext)
  extends JsonSupport {

  val corsSettings = allowedOrigin match {
    case "*" => CorsSettings.defaultSettings
    case _ =>
      val range = HttpOriginRange(HttpOrigin(allowedOrigin))
      CorsSettings.defaultSettings.copy(allowedOrigins = range)
  }

  val route =
    CorsDirectives.cors(corsSettings) {
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
              complete(service.create(carAdvert))
            }
          } ~
          put {
            entity(as[CarAdvert]) { carAdvert =>
              onSuccess(service.update(carAdvert)) {
                case Some(advert) => complete(advert.toJson)
                case _ => complete(NotFound)
              }
            }
          } ~
          delete {
            pathPrefix(IntNumber) { id =>
              onSuccess(service.delete(id)) {
                case 0 => complete(NotFound)
                case _ => complete(NoContent)
              }
            }
          }
      }
    }
}