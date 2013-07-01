libraryDependencies += "com.h2database" % "h2" % "[1.3,)"

addSbtPlugin("com.github.seratch" %% "scalikejdbc-mapper-generator" % "1.6.4")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

logLevel := Level.Warn

addSbtPlugin("play" % "sbt-plugin" % "2.1.1")

