ThisBuild / publishTo := {
  val artifactory = "https://art.aivax.link/artifactory/"
  if (isSnapshot.value)
    Some("snapshots".at(artifactory + "libs-snapshot-local/"))
  else
    Some("releases".at(artifactory + "libs-release-local/"))
}
