package apps
package multicast
package core
package transit

import cats.effect.{ Async, Resource }
import encryption.symmetric.data.Base64DataEncryptor
import encryption.symmetric.data.aws.AwsKmsDataEncryptor
import model.authentication.Platform
import model.authentication.Platform.Aws

sealed trait DataEncryptor[F[_]] {

  def encrypt(plainText: String): F[String]

  def decrypt(encryptedText: String): F[String]
}
case object DataEncryptor {

  def create[F[_]](enabled: Boolean, platform: Platform)(implicit
    F: Async[F]
  ): Resource[F, DataEncryptor[F]] =
    for {
      awsEncryptor <- (enabled, platform) match {
                        case (true, p: Aws) =>
                          AwsKmsDataEncryptor.create(
                            platform = p
                          )
                        case _              =>
                          Base64DataEncryptor.create
                      }
    } yield new DataEncryptor[F] {
      override def encrypt(plainText: String): F[String] = awsEncryptor.encrypt(plainText = plainText)

      override def decrypt(encryptedText: String): F[String] = awsEncryptor.decrypt(encryptedText = encryptedText)
    }
}
