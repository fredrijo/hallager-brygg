// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

addSbtPlugin("com.jamesward" %% "play-auto-refresh" % "0.0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.1")