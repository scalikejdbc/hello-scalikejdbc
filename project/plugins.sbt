resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)
libraryDependencies += "com.h2database" % "h2" % "1.4.191"

addSbtPlugin("org.scalariform"   % "sbt-scalariform"              % "1.8.2")
addSbtPlugin("org.scalikejdbc"   % "scalikejdbc-mapper-generator" % "3.2.2")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"                   % "2.6.16")
addSbtPlugin("com.typesafe.sbt"  % "sbt-coffeescript"             % "1.0.2")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"                  % "0.3.4")
