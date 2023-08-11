import sbt._

object Dependencies {
  // Versions
  lazy val catsEffectVersion            = "3.5.1"
  lazy val fs2Version                   = "3.8.0"
  lazy val log4catsVersion              = "2.6.0"
  lazy val http4sVersion                = "0.23.22"
  lazy val circeVersion                 = "0.14.5"
  lazy val pureconfigVersion            = "0.17.2"
  lazy val fs2DataCsvVersion            = "1.8.0"
  lazy val `ip4s-version`               = "3.3.0"
  lazy val betterFilesVersion           = "3.9.2"
  lazy val `squants-version`            = "1.8.3"
  lazy val scalatestVersion             = "3.2.16"
  lazy val munitVersion                 = "0.7.29"
  lazy val munitCatsEffectVersion       = "1.0.7"
  lazy val log4jVersion                 = "2.20.0"
  lazy val `aws-sdk-core-version`       = "1.12.429"
  lazy val `aws-encryption-sdk-version` = "2.4.0"
  lazy val `bouncycastle-version`       = "1.70"
  lazy val `jaxb-api-version`           = "2.3.1"

  // Libraries
  val fs2Core = "co.fs2" %% "fs2-core" % fs2Version
  val fs2Io   = "co.fs2" %% "fs2-io"   % fs2Version

  val log4catsCore      = "org.typelevel"           %% "log4cats-core"    % log4catsVersion
  val log4catsSlf4j     = "org.typelevel"           %% "log4cats-slf4j"   % log4catsVersion
  val log4jSlf4jBinding = "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion

  val http4sDsl         = "org.http4s" %% "http4s-dsl"          % http4sVersion
  val http4sCirce       = "org.http4s" %% "http4s-circe"        % http4sVersion
  val http4sEmberClient = "org.http4s" %% "http4s-ember-client" % http4sVersion

  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val circeLiteral = "io.circe" %% "circe-literal" % circeVersion
  val circeParser  = "io.circe" %% "circe-parser"  % circeVersion

  val pureconfigCore       = "com.github.pureconfig" %% "pureconfig"             % pureconfigVersion
  val pureconfigCats       = "com.github.pureconfig" %% "pureconfig-cats"        % pureconfigVersion
  val pureconfigCatsEffect = "com.github.pureconfig" %% "pureconfig-cats-effect" % pureconfigVersion
  val pureconfigCirce      = "com.github.pureconfig" %% "pureconfig-circe"       % pureconfigVersion
  val pureconfigHttp4s     = "com.github.pureconfig" %% "pureconfig-http4s"      % pureconfigVersion
  val pureconfigFs2        = "com.github.pureconfig" %% "pureconfig-fs2"         % pureconfigVersion
  val pureconfigIp4s       = "com.github.pureconfig" %% "pureconfig-ip4s"        % pureconfigVersion

  val fs2DataCsv        = "org.gnieh" %% "fs2-data-csv"         % fs2DataCsvVersion
  val fs2DataCsvGeneric = "org.gnieh" %% "fs2-data-csv-generic" % fs2DataCsvVersion

  val ip4s = "com.comcast" %% "ip4s-core" % `ip4s-version`

  val betterFiles = "com.github.pathikrit" %% "better-files" % betterFilesVersion
  val squants     = "org.typelevel"        %% "squants"      % `squants-version`

  // Java Libraries
  val `aws-sdk-core`       = "com.amazonaws"    % "aws-java-sdk-core"       % `aws-sdk-core-version`
  val `aws-sdk-kms`        = "com.amazonaws"    % "aws-java-sdk-kms"        % `aws-sdk-core-version`
  val `aws-encryption-sdk` = "com.amazonaws"    % "aws-encryption-sdk-java" % `aws-encryption-sdk-version`
  val `bouncycastle`       = "org.bouncycastle" % "bcprov-ext-jdk15on"      % `bouncycastle-version`
  val `jaxb-api`           = "javax.xml.bind"   % "jaxb-api"                % `jaxb-api-version`

  // Test Libraries
  val catsEffectTestkit = "org.typelevel" %% "cats-effect-testkit" % catsEffectVersion
  val munitCore         = "org.scalameta" %% "munit"               % munitVersion
  val munitCatsEffect   = "org.typelevel" %% "munit-cats-effect-3" % munitCatsEffectVersion
  val scalatest         = "org.scalatest" %% "scalatest"           % scalatestVersion

  // Groups
  val loggingDeps =
    Seq(log4catsCore, log4catsSlf4j, log4jSlf4jBinding)

  val munitDeps =
    Seq(munitCore % Test, catsEffectTestkit % Test, munitCatsEffect % Test)

  val testDeps =
    Seq(scalatest % Test)
}
