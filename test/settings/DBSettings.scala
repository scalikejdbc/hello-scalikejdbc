package settings

import misc._
import scalikejdbc._, config._, SQLInterpolation._

trait DBSettings {
  DBSettings.initialize()
}

object DBSettings {

  private var isInitialized = false

  def initialize(): Unit = this.synchronized {
    if (isInitialized) return
    DBs.setupAll()
    GlobalSettings.loggingSQLErrors = false
    GlobalSettings.sqlFormatter = SQLFormatterSettings("devteam.misc.HibernateSQLFormatter")
    DBInitializer.run()
    isInitialized = true
  }

}

