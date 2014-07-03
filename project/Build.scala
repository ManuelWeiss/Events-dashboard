import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "events-dashboard"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
      "org.webjars" %% "webjars-play" % "2.2.2-1",
      "org.webjars" % "bootstrap" % "3.1.1",
      "org.webjars" % "angularjs" % "1.3.0-beta.4"
      )

  val main = play.Project(appName, appVersion, appDependencies).settings()
}
