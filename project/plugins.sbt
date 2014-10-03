resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "com.h2database" % "h2" % "1.4.181"

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.1.2")

logLevel := Level.Warn

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.6")

