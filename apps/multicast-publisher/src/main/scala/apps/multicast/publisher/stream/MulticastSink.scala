package apps
package multicast
package publisher
package stream

import cats.effect.{ Resource, Sync }
import cats.implicits.*
import com.comcast.ip4s.*
import common.multicast.socket.MulticastSocket
import fs2.*
import fs2.io.net.{ Datagram, Network }
import model.multicast.DatagramWrapper.toJson
import model.multicast.{ DatagramWrapper, MulticastSettings }
import org.typelevel.log4cats.Logger

import java.nio.charset.StandardCharsets

sealed trait MulticastSink[F[_]] {

  def publish: Pipe[F, DatagramWrapper, DatagramWrapper]

}
case object MulticastSink {

  def create[F[_]: Network](multicastSettings: MulticastSettings)(implicit
    F: Sync[F],
    logger: Logger[F]
  ): Resource[F, MulticastSink[F]] =
    for {
      multicastSocket <- MulticastSocket.create(multicastSettings = multicastSettings)
    } yield new MulticastSink[F] {
      override def publish: Pipe[F, DatagramWrapper, DatagramWrapper] =
        _.evalMap { datagramWrapper =>
          for {
            _ <- logger.info(s"Publishing: $datagramWrapper")
          } yield datagramWrapper
        }
          .evalMap { datagramWrapper =>
            for {
              datagramWrapperJson <- F.delay(toJson(datagramWrapper))
              datagram            <- F.delay {
                                       Datagram(
                                         SocketAddress(multicastSettings.group.address, multicastSettings.port),
                                         Chunk.array(datagramWrapperJson.getBytes(StandardCharsets.UTF_8))
                                       )
                                     }

            } yield datagram
          }
          .through(multicastSocket.socket.writes)
    }
}
