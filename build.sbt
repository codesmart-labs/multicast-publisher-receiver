import Dependencies._

lazy val scala213               = "2.13.10"
lazy val scala320               = "3.2.1"
lazy val supportedScalaVersions = List(scala320, scala213)

inThisBuild(
  List(
    organization := "com.github.rehanone",
    homepage     := Some(url("https://github.com/rehanone/fs2-multicast")),
    scmInfo      := Some(
      ScmInfo(url("https://github.com/rehanone/fs2-multicast"), "git@github.com:rehanone/fs2-multicast.git")
    ),
    developers   := List(
      Developer("rehanone", "Rehan Mahmood", "_ at gmail dot com", url("https://github.com/rehanone"))
    ),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
  )
)
ThisBuild / scalaVersion               := scala213
ThisBuild / crossScalaVersions         := supportedScalaVersions
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.graalvm("20.3.1", "11"))
ThisBuild / versionScheme              := Some("early-semver")

ThisBuild / githubWorkflowBuildPreamble ++= Seq(
  WorkflowStep.Run(
    List(
      "chmod -R 777 ./ftp-home/",
      "docker-compose -f \"docker-compose.yml\" up -d --build",
      "chmod -R 777 ./ftp-home/sftp/home/foo/dir1"
    ),
    name = Some("Start containers")
  )
)
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("check", "test"))
)

//sbt-ci-release settings
ThisBuild / githubWorkflowPublishPreamble := Seq(
  WorkflowStep.Use(UseRef.Public("olafurpg", "setup-gpg", "v3"))
)

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.StartsWith(Ref.Branch("master")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)
ThisBuild / githubWorkflowPublish               := Seq(WorkflowStep.Sbt(List("ci-release")))
ThisBuild / githubWorkflowEnv ++= List(
  "PGP_PASSPHRASE",
  "PGP_SECRET",
  "SONATYPE_PASSWORD",
  "SONATYPE_USERNAME"
).map(envKey => envKey -> s"$${{ secrets.$envKey }}").toMap

val compilerOptionsCommon = Seq(
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-language:higherKinds",
  "-language:postfixOps"
)

val compilerOptionsScala212 = Seq("-Xlint", "-Ypartial-unification")

val compilerOptionsScala213 = Seq(
  "-Xsource:3",
  "-Xlint:adapted-args",
  "-Xlint:nullary-unit",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:doc-detached",
  "-Xlint:private-shadow",
  "-Xlint:type-parameter-shadow",
  "-Xlint:poly-implicit-overload",
  "-Xlint:option-implicit",
  "-Xlint:delayedinit-select",
  "-Xlint:package-object-classes",
  "-Xlint:stars-align",
  "-Xlint:strict-unsealed-patmat",
  "-Xlint:constant",
  "-Xlint:unused",
  "-Xlint:nonlocal-return",
  "-Xlint:implicit-not-found",
  "-Xlint:serial",
  "-Xlint:valpattern",
  "-Xlint:eta-zero",
  "-Xlint:eta-sam",
  "-Xlint:deprecation",
  //        "-Xlint:byname-implicit",
  "-Xlint:recurse-with-default",
  "-Xlint:unit-special",
  "-Xlint:multiarg-infix",
  "-Xlint:implicit-recursion",
  "-Xlint:universal-methods"
)

val compilerOptionsScala3 = Seq()

lazy val `model-alphavantage` = (project in file("models/model-alphavantage"))
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
      :+ fs2Core
      :+ circeGeneric
      :+ circeParser
      :+ fs2Io % Test
  )

lazy val `model-multicast` = (project in file("models/model-multicast"))
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
      :+ fs2Core
      :+ circeGeneric
      :+ circeParser
      :+ ip4s
      :+ fs2Io % Test
  )

lazy val `model-authentication` = (project in file("models/model-authentication"))
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten
  )

lazy val `common-multicast` = (project in file("common/common-multicast"))
  .dependsOn(`model-multicast`)
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
      :+ fs2Core
      :+ fs2Io
  )

lazy val `common-pretty-cli` = (project in file("common/common-pretty-cli"))
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten
  )

lazy val `common-time` = (project in file("common/common-time"))
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
  )

lazy val `encryption-core` = (project in file("encryption/encryption-core"))
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
      :+ fs2Core
  )

lazy val `encryption-aws` = (project in file("encryption/encryption-aws"))
  .dependsOn(`model-authentication`, `encryption-core`)
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
      ++ loggingDeps
      :+ fs2Core
      :+ fs2Io
      :+ `aws-sdk-core`
      :+ `aws-sdk-kms`
      :+ `aws-encryption-sdk`
      :+ `bouncycastle`
      :+ `jaxb-api`
  )

lazy val `multicast-core` = (project in file("apps/multicast-core"))
  .dependsOn(`model-multicast`, `model-authentication`, `encryption-aws`)
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
      :+ fs2Core
      :+ fs2Io
      :+ circeGeneric
      :+ circeParser
  )

lazy val `multicast-publisher` = (project in file("apps/multicast-publisher"))
  .dependsOn(`common-multicast`, `model-alphavantage`, `multicast-core`)
  .enablePlugins(JavaAppPackaging)
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    crossScalaVersions              := Seq(scala213),
    Test / fork                     := true,
    Test / parallelExecution        := false,
    publishMavenStyle               := true,
    Compile / mainClass             := Some("apps.multicast.publisher.PublisherApp"),
    Compile / discoveredMainClasses := Seq(),
    bashScriptConfigLocation        := Some("${app_home}/../conf/linux.ini"),
    batScriptConfigLocation         := Some("%APP_HOME%\\conf\\windows.ini"),
    topLevelDirectory               := Some(packageName.value)
  )
  .settings(
    libraryDependencies ++= munitDeps
      ++ loggingDeps
      :+ fs2Core
      :+ fs2Io
      :+ http4sDsl
      :+ http4sEmberClient
      :+ http4sCirce
      :+ circeGeneric
      :+ circeLiteral
      :+ pureconfigCore
      :+ pureconfigCatsEffect
      :+ pureconfigHttp4s
      :+ pureconfigIp4s
      :+ fs2DataCsv
      :+ fs2DataCsvGeneric
  )

lazy val `multicast-receiver` = (project in file("apps/multicast-receiver"))
  .dependsOn(`common-multicast`, `model-alphavantage`, `common-pretty-cli`, `common-time`, `multicast-core`)
  .enablePlugins(JavaAppPackaging)
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    crossScalaVersions              := Seq(scala213),
    Test / fork                     := true,
    Test / parallelExecution        := false,
    publishMavenStyle               := true,
    Compile / mainClass             := Some("apps.multicast.receiver.ReceiverApp"),
    Compile / discoveredMainClasses := Seq(),
    bashScriptConfigLocation        := Some("${app_home}/../conf/linux.ini"),
    batScriptConfigLocation         := Some("%APP_HOME%\\conf\\windows.ini"),
    topLevelDirectory               := Some(packageName.value)
  )
  .settings(
    libraryDependencies ++= munitDeps
      :+ fs2Core
      :+ fs2Io
      :+ pureconfigCore
      :+ pureconfigCatsEffect
      :+ pureconfigHttp4s
  )

lazy val `multicast-snooper` = (project in file("apps/multicast-snooper"))
  .dependsOn(`common-multicast`, `common-time`, `multicast-core`)
  .enablePlugins(JavaAppPackaging)
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    crossScalaVersions              := Seq(scala213),
    Test / fork                     := true,
    Test / parallelExecution        := false,
    publishMavenStyle               := true,
    Compile / mainClass             := Some("apps.multicast.snooper.SnooperApp"),
    Compile / discoveredMainClasses := Seq(),
    bashScriptConfigLocation        := Some("${app_home}/../conf/linux.ini"),
    batScriptConfigLocation         := Some("%APP_HOME%\\conf\\windows.ini"),
    topLevelDirectory               := Some(packageName.value)
  )
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((2, n)) if n < 13  => compilerOptionsScala212
        case Some((2, n)) if n >= 13 => compilerOptionsScala213
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= munitDeps
      ++ loggingDeps
      :+ fs2Core
      :+ fs2Io
      :+ pureconfigCore
      :+ pureconfigCatsEffect
      :+ pureconfigHttp4s
  )

lazy val root = (project in file("."))
  .aggregate(
    `model-alphavantage`,
    `model-authentication`,
    `model-multicast`,
    `common-multicast`,
    `common-pretty-cli`,
    `common-time`,
    `encryption-core`,
    `encryption-aws`,
    `multicast-core`,
    `multicast-publisher`,
    `multicast-receiver`,
    `multicast-snooper`
  )
  .settings(
    name               := "multicast-publisher-receiver-fs2",
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    publish / skip     := false
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")