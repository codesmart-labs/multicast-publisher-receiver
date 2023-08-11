package apps
package multicast
package publisher

import cats.effect.{ IO, IOApp }
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.{ LoggerFactory, SelfAwareStructuredLogger }
import fs2.*

object PublisherApp extends IOApp.Simple {

  def run: IO[Unit] = {

    implicit val logging: LoggerFactory[IO]            = Slf4jFactory.create[IO]
    implicit val logger: SelfAwareStructuredLogger[IO] = LoggerFactory[IO].getLogger

    Stream
      .resource {
        Orchestrator
          .create[IO]
      }
      .flatMap(_.run)
      .compile
      .drain
  }
}
