package apps
package multicast
package snooper

import apps.multicast.snooper.settings.AppSettings
import cats.effect.{ Async, Resource }
import cats.implicits.*
import common.multicast.socket.MulticastSocket
import fs2.Stream
import fs2.io.net.Network

import java.nio.charset.StandardCharsets

sealed trait MulticastClient[F[_]] {

  def run: Stream[F, Unit]
}
case object MulticastClient {

  def build[F[_]: Network](implicit F: Async[F]): Resource[F, MulticastClient[F]] =
    for {
      appSettings       <- AppSettings.load
      multicastSettings <- Resource.pure(appSettings.settings.multicast)
      multicastSocket   <- MulticastSocket.create(multicastSettings = multicastSettings)
    } yield new MulticastClient[F] {
      override def run: Stream[F, Unit] =
        Stream
          .eval {
            F.delay(println(s"Waiting for messages on socket: ${multicastSettings.group}:${multicastSettings.port}"))
          }
          .flatMap { _ =>
            multicastSocket.socket.reads.evalMap { packet =>
              for {
                datagramPacket <- F.delay(new String(packet.bytes.toArray, StandardCharsets.UTF_8))
                datagramJson   <- F.async_ { cb: (Either[Throwable, String] => Unit) =>
                                    import io.circe.parser.*

                                    cb {
                                      parse(datagramPacket).map(_.spaces2)
                                    }
                                  }
                _              <- F.delay(println("------"))
                _              <- F.delay(println(datagramJson))
              } yield ()
            }.drain
          }
    }
}
