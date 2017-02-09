package me.caradverts.config

import com.typesafe.config.Config

class AppConfig(config: Config) {
  val http: EndpointConfig = new EndpointConfig(config.getConfig("http"))
  val db: EndpointConfig = new EndpointConfig(config.getConfig("db"))
}

class EndpointConfig(config: Config) {
  val host: String = config.getString("host")
  val port: Int = config.getInt("port")
}
