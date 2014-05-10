resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "com.h2database" % "h2" % "1.4.178"

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.0.0")

logLevel := Level.Warn

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.3")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.5")

