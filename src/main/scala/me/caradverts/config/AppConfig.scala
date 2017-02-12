package me.caradverts.config

import com.typesafe.config.Config

class AppConfig(config: Config) {
  val http: EndpointConfig = new EndpointConfig(config.getConfig("http"))
  val db: StorageConfig = config.getString("storage.type") match {
    case "mongo" => MongoStorageConfig(
      config.getString("storage.mongo.host"),
      config.getInt("storage.mongo.port"),
      config.getString("storage.mongo.db-name")
    )
    case "in-mem" => InMemStorageConfig
  }
}

case class EndpointConfig(config: Config) {
  val host: String = config.getString("host")
  val port: Int = config.getInt("port")
  val allowedOrigin: String = config.getString("allowed-origin")
}

trait StorageConfig

object InMemStorageConfig extends StorageConfig

case class MongoStorageConfig(host: String, port: Int, databaseName: String) extends StorageConfig
