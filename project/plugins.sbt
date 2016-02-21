resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)
libraryDependencies += "com.h2database" % "h2" % "1.4.191"

addSbtPlugin("org.scalariform"   % "sbt-scalariform"              % "1.6.0")
addSbtPlugin("org.scalikejdbc"   % "scalikejdbc-mapper-generator" % "2.3.5")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"                   % "2.4.6")
addSbtPlugin("com.typesafe.sbt"  % "sbt-coffeescript"             % "1.0.0")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"                  % "0.1.10")
