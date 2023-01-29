package encryption
package symmetric
package data
package aws

import cats.effect.{ Async, Resource }
import cats.implicits.*
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider
import com.amazonaws.encryptionsdk.{ AwsCrypto, CommitmentPolicy }
import encryption.symmetric.data.SymmetricDataEncryptor
import model.authentication.Platform

import java.nio.charset.StandardCharsets
import java.util.Base64

sealed trait AwsKmsDataEncryptor[F[_]] extends SymmetricDataEncryptor[F]

case object AwsKmsDataEncryptor {

  def create[F[_]](platform: Platform.Aws)(implicit
    F: Async[F]
  ): Resource[F, AwsKmsDataEncryptor[F]] =
    for {
      credentials        <- Resource.pure {
                              new BasicAWSCredentials(
                                platform.authentication.accessKeyId,
                                platform.authentication.secretAccessKey
                              )
                            }
      jEncryptionContext <- Resource.pure {
                              import scala.jdk.CollectionConverters.*
                              platform.encryptionContext.asJava
                            }
      awsCrypto          <- Resource.pure {
                              AwsCrypto
                                .builder()
                                .withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
                                .build()
                            }
      keyProvider        <- Resource.pure {
                              KmsMasterKeyProvider
                                .builder()
                                .withCredentials(credentials)
                                .withDefaultRegion(platform.authentication.awsRegion)
                                .buildStrict(platform.keyArns*)
                            }
      base64Encoder      <- Resource.pure(Base64.getEncoder)
      base64Decoder      <- Resource.pure(Base64.getDecoder)
    } yield new AwsKmsDataEncryptor[F] {
      override def encrypt(plainText: String): F[String] =
        for {
          plainTextBytes <- F.delay(plainText.getBytes(StandardCharsets.UTF_8))
          encryptResult  <- F.delay {
                              awsCrypto.encryptData(keyProvider, plainTextBytes, jEncryptionContext)
                            }
          ciphertext     <- F.delay(encryptResult.getResult)
        } yield base64Encoder.encodeToString(ciphertext)

      override def decrypt(encryptedText: String): F[String] =
        for {
          decodedBytes  <- F.delay(base64Decoder.decode(encryptedText))
          decryptResult <- F.delay(awsCrypto.decryptData(keyProvider, decodedBytes))
        } yield new String(decryptResult.getResult, StandardCharsets.UTF_8)
    }
}
