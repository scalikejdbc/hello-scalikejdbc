resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "com.h2database" % "h2" % "1.4.182"

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.1.4")

logLevel := Level.Warn

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.6")

