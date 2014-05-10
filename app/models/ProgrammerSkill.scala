package models

import scalikejdbc._

case class ProgrammerSkill(programmerId: Long, skillId: Long)

object ProgrammerSkill extends SQLSyntaxSupport[ProgrammerSkill] {
  val ps = ProgrammerSkill.syntax("ps")
}

