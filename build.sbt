name := "WorkHoursTracker"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
    "com.github.hypfvieh" % "dbus-java" % "3.2.0",
    "org.slf4j" % "slf4j-simple" % "1.7.29"
)

mainClass := Some("tel.schich.workhourstracker.Main")

assemblyMergeStrategy in assembly := {
    case PathList(path) if path.endsWith("module-info.class") => MergeStrategy.discard
    case p =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(p)
}
