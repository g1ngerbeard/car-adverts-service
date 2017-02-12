package me.caradverts.common

import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodProcess, MongodStarter}
import org.scalatest.{BeforeAndAfterEach, Suite}

trait EmbeddedMongoSupport extends BeforeAndAfterEach {
  this: Suite =>

  private var mongodProcess: Option[MongodProcess] = None

  override def beforeEach() {
    val starter = MongodStarter.getDefaultInstance
    val mongodConfig = new MongodConfig(Version.Main.V2_2, 12345, false)
    mongodProcess = Some(starter.prepare(mongodConfig).start)
  }

  override def afterEach() {
    mongodProcess.foreach(_.stop())
  }
}



