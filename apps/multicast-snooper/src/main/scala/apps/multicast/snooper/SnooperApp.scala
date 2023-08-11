package apps
package multicast
package snooper

import cats.effect.{ IO, IOApp }
import fs2.*

object SnooperApp extends IOApp.Simple {

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
