lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .settings(scalikejdbcSettings:_*) // http://scalikejdbc.org/documentation/setup.html

name := "hello-scalikejdbc"

version := "0.1"

scalaVersion := "2.11.4"

lazy val scalikejdbcVersion = "2.1.4"

lazy val h2Version = "1.4.182"

libraryDependencies ++= Seq(
  "org.scalikejdbc"      %% "scalikejdbc"                     % scalikejdbcVersion,
  "org.scalikejdbc"      %% "scalikejdbc-config"              % scalikejdbcVersion,
  "org.scalikejdbc"      %% "scalikejdbc-interpolation"       % scalikejdbcVersion,
  "org.scalikejdbc"      %% "scalikejdbc-play-plugin"         % "2.3.3",
  "org.scalikejdbc"      %% "scalikejdbc-play-fixture-plugin" % "2.3.3",
  "com.h2database"       %  "h2"                              % h2Version,
  "org.hibernate"        %  "hibernate-core"                  % "4.2.3.Final",
  "org.json4s"           %% "json4s-ext"                      % "3.2.10",
  "com.github.tototoshi" %% "play-json4s-native"              % "0.3.0",
  "com.github.tototoshi" %% "play-flyway"                     % "1.1.2",
  "org.scalikejdbc"      %% "scalikejdbc-test"                % scalikejdbcVersion  % "test"
)

initialCommands := """
import scalikejdbc._, config._
import models._, utils._
DBs.setupAll
DBInitializer.run()
implicit val autoSession = AutoSession
val (p, c, s, ps) = (Programmer.syntax("p"), Company.syntax("c"), Skill.syntax("s"), ProgrammerSkill.syntax("ps"))
"""

