package encryption
package symmetric
package data

import cats.effect.{ Resource, Sync }

trait SymmetricDataEncryptor[F[_]] {

  def encrypt(plainText: String): F[String]
  def decrypt(encryptedText: String): F[String]

}

case object SymmetricDataEncryptor {

  def create[F[_]](implicit F: Sync[F]): Resource[F, SymmetricDataEncryptor[F]] =
    Resource.eval {
      F.delay {
        new SymmetricDataEncryptor[F] {
          override def encrypt(plainText: String): F[String] = F.delay(plainText)

          override def decrypt(encryptedText: String): F[String] = F.delay(encryptedText)
        }
      }
    }
}
