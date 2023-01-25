package encryption.symmetric.data

import cats.effect.IO
import munit.CatsEffectSuite

class Base64DataEncryptorSpec extends CatsEffectSuite {

  def checkEncryption(name: String, plainText: String)(implicit loc: munit.Location): Unit =
    test(name) {
      Base64DataEncryptor
        .create[IO]
        .use { encryptor =>
          for {
            encryptedText <- encryptor.encrypt(plainText = plainText)
            result        <- encryptor.decrypt(encryptedText = encryptedText)
          } yield assertEquals(result, plainText)
        }
    }

  checkEncryption(
    name = "check encoding works using Base64",
    plainText = "This is a secret message!"
  )
}
