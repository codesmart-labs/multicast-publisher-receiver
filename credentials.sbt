import java.io.File

ThisBuild / credentials += {
  val localCredentialsPath  = Path.userHome / ".sbt" / ".credentials"
  val gitlabCredentialsPath = new File(".secure_files/sbt_credentials")
  if (localCredentialsPath.exists())
    Credentials(localCredentialsPath)
  else
    Credentials(gitlabCredentialsPath)
}
