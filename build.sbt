lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .settings(
    name := "hello-scalikejdbc",
    version := "0.1",
    scalaVersion := "2.11.5",
    resolvers += "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
    libraryDependencies ++= Seq(
      "org.scalikejdbc"      %% "scalikejdbc"                     % scalikejdbcVersion,
      "org.scalikejdbc"      %% "scalikejdbc-config"              % scalikejdbcVersion,
      "org.scalikejdbc"      %% "scalikejdbc-play-plugin"         % scalikejdbcPlayVersion,
      "org.scalikejdbc"      %% "scalikejdbc-play-fixture-plugin" % scalikejdbcPlayVersion,
      "com.h2database"       %  "h2"                              % h2Version,
      "org.hibernate"        %  "hibernate-core"                  % "4.3.7.Final",
      "org.json4s"           %% "json4s-ext"                      % "3.2.11",
      "com.github.tototoshi" %% "play-json4s-native"              % "0.3.0",
      "com.github.tototoshi" %% "play-flyway"                     % "1.2.0",
      "org.scalikejdbc"      %% "scalikejdbc-test"                % scalikejdbcVersion  % "test"
    ),
    initialCommands := """
      import scalikejdbc._, config._
      import models._, utils._
      DBs.setupAll
      DBInitializer.run()
      implicit val autoSession = AutoSession
      val (p, c, s, ps) = (Programmer.syntax("p"), Company.syntax("c"), Skill.syntax("s"), ProgrammerSkill.syntax("ps"))
    """
  )
  .settings(scalikejdbcSettings:_*) // http://scalikejdbc.org/documentation/setup.html

lazy val scalikejdbcVersion = "2.2.+"
lazy val scalikejdbcPlayVersion = "2.3.+"
lazy val h2Version = "1.4.+"

