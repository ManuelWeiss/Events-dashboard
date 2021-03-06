import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "events-dashboard"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
      "org.webjars" %% "webjars-play" % "2.2.2-1"
      )

  val main = play.Project(appName, appVersion, appDependencies).settings()
}
