resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "com.h2database" % "h2" % "[1.3,)"

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "1.7.3")

logLevel := Level.Warn

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.2")

