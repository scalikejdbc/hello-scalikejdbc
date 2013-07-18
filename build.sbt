initialCommands := """
import scalikejdbc._,config._,SQLInterpolation._
import models._, utils._
DBs.setupAll
DBInitializer.run()
implicit val autoSession = AutoSession
val (p, c, s, ps) = (Programmer.syntax("p"), Company.syntax("c"), Skill.syntax("s"), ProgrammerSkill.syntax("ps"))
"""

scalikejdbcSettings

