import sbt._, Keys._
import play.Project._

object DevTeamBuild extends Build {

  lazy val scalikejdbcVersion = "2.0.0"
  lazy val h2Version = "1.4.178"

  lazy val app = {
    val appName         = "hello-scalikejdbc"
    val appVersion      = "0.1"
    val appDependencies = Seq(
      "org.scalikejdbc"      %% "scalikejdbc"                     % scalikejdbcVersion,
      "org.scalikejdbc"      %% "scalikejdbc-config"              % scalikejdbcVersion,
      "org.scalikejdbc"      %% "scalikejdbc-interpolation"       % scalikejdbcVersion,
      "org.scalikejdbc"      %% "scalikejdbc-play-plugin"         % "2.2.0",
      "org.scalikejdbc"      %% "scalikejdbc-play-fixture-plugin" % "2.2.0",
      "com.h2database"       %  "h2"                              % h2Version,
      "org.hibernate"        %  "hibernate-core"                  % "4.2.3.Final",
      "org.json4s"           %% "json4s-ext"                      % "3.2.9",
      "com.github.tototoshi" %% "play-json4s-native"              % "0.2.0",
      "com.github.tototoshi" %% "play-flyway"                     % "1.0.3",
      "org.scalikejdbc"      %% "scalikejdbc-test"                % scalikejdbcVersion  % "test",
      "org.specs2"           %% "specs2"                          % "2.1"               % "test"
    )
    play.Project(appName, appVersion, appDependencies).settings(
      scalaVersion in ThisBuild := "2.10.3",
      conflictWarning := ConflictWarning.disable
    )
  }

}
