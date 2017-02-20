lazy val root = (project in file(".")).
  settings(
    scalaVersion := "2.11.8",
    organization := "com.hnrklssn",
    name := "momondo-massiveness",
    libraryDependencies ++= dependencies,
    resolvers += "BFil Nexus Releases" at "http://nexus.b-fil.com/nexus/content/repositories/releases/"
  )
  
  lazy val dependencies = Seq(
    //"com.bfil" %% "scalescrape" % "0.3.0",
    //"org.jsoup" % "jsoup" % "1.7.2",
    "org.scalatest" %% "scalatest" % "3.0.1",
    //"org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0",
    "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.0.1",
    "com.typesafe.akka" %% "akka-actor" % "2.5-M1"
  )