package modules

import javax.inject._
import scalikejdbc._

@Singleton
class Global {
}

class GlobalModule extends com.google.inject.AbstractModule {
  def configure() = {
    bind(classOf[Global]).asEagerSingleton
  }
}
