name := "s2-geometry-library-java-sbt"
organization := "com.colisweb"
version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "r05",
  "org.hamcrest" % "hamcrest-all" % "1.3",
  "junit" % "junit" % "4.10",
  "com.google.code.findbugs" % "jsr305" % "3.0.0"
)

publishArtifact in (Compile, packageDoc) := false