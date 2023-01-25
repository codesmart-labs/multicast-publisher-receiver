package encryption
package symmetric
package data

import cats.effect.{ Resource, Sync }
import cats.implicits.*

import java.nio.charset.StandardCharsets
import java.util.Base64

trait Base64DataEncryptor[F[_]] extends SymmetricDataEncryptor[F] {

  def encrypt(plainText: String): F[String]
  def decrypt(encryptedText: String): F[String]

}

case object Base64DataEncryptor {

  def create[F[_]](implicit F: Sync[F]): Resource[F, SymmetricDataEncryptor[F]] =
    for {
      base64Encoder <- Resource.pure(Base64.getEncoder)
      base64Decoder <- Resource.pure(Base64.getDecoder)
    } yield new Base64DataEncryptor[F] {
      override def encrypt(plainText: String): F[String] =
        for {
          plainTextBytes <- F.delay(plainText.getBytes(StandardCharsets.UTF_8))
        } yield base64Encoder.encodeToString(plainTextBytes)

      override def decrypt(encryptedText: String): F[String] =
        for {
          decodedBytes <- F.delay(base64Decoder.decode(encryptedText))
        } yield new String(decodedBytes, StandardCharsets.UTF_8)
    }
}
