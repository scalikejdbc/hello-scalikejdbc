package misc

import scalikejdbc._

class HibernateSQLFormatter extends SQLFormatter {

  private val formatter = new org.hibernate.engine.jdbc.internal.BasicFormatterImpl

  override def format(sql: String) = formatter.format(sql)
}

