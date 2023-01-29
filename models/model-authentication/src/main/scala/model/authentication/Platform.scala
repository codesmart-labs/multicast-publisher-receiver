package model.authentication

import model.authentication.Platform.Aws.Authentication

sealed trait Platform

case object Platform {
  case object Local extends Platform
  case class Aws(
    keyArns: List[String],
    encryptionContext: Map[String, String],
    authentication: Authentication
  ) extends Platform

  case object Aws {
    case class Authentication(awsRegion: String, accessKeyId: String, secretAccessKey: String)
  }
}
