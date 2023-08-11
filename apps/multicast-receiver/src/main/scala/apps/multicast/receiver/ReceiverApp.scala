package apps
package multicast
package receiver

import cats.effect.{ IO, IOApp }
import fs2.*
object ReceiverApp extends IOApp.Simple {

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
