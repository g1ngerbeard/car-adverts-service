package me.caradverts.config

import com.typesafe.config.Config

class AppConfig(config: Config) {
  val http: EndpointConfig = new EndpointConfig(config.getConfig("http"))
  val db: StorageConfig = config.getString("storage.type") match {
    case "mongo" => MongoStorageConfig(
      config.getString("storage.mongo.host"),
      config.getInt("storage.mongo.port")
    )
    case "in-mem" => InMemStorageConfig
  }
}

case class EndpointConfig(config: Config) {
  val host: String = config.getString("host")
  val port: Int = config.getInt("port")
}

trait StorageConfig

object InMemStorageConfig extends StorageConfig

case class MongoStorageConfig(host: String, port: Int) extends StorageConfig
