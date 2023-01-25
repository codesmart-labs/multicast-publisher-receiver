package apps
package multicast
package snooper

import cats.effect.{ IO, IOApp }
import fs2.*
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
object SnooperApp extends IOApp.Simple {

  implicit val logging: LoggerFactory[IO] = Slf4jFactory[IO]

  def run: IO[Unit] =
    Stream
      .resource {
        MulticastClient.build[IO]
      }
      .flatMap { client =>
        client.run
      }
      .compile
      .drain
}
