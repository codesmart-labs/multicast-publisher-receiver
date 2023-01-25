package apps
package multicast
package snooper

import apps.multicast.core.serialization.DatagramUnrapper
import cats.effect.{ Async, Resource }
import cats.implicits.*
import common.multicast.socket.MulticastSocket
import fs2.*
import fs2.io.net.Network
import multicast.core.transit.DataEncryptor
import multicast.snooper.message.{ MessageFormatter, MessageTransformer }
import multicast.snooper.settings.AppSettings

import java.nio.charset.StandardCharsets

sealed trait MulticastClient[F[_]] {

  def run: Stream[F, Unit]
}
case object MulticastClient {

  def build[F[_]: Network](implicit F: Async[F]): Resource[F, MulticastClient[F]] =
    for {
      appSettings        <- AppSettings.load
      multicastSettings  <- Resource.pure(appSettings.settings.multicast)
      multicastSocket    <- MulticastSocket.create(multicastSettings = multicastSettings)
      dataEncryptor      <-
        DataEncryptor.create(appSettings.settings.encryption.enabled, appSettings.settings.encryption.platform)
      datagramUnrapper   <- DatagramUnrapper.create
      messageTransformer <- MessageTransformer.create
      messageFormatter   <- MessageFormatter.create
    } yield new MulticastClient[F] {
      override def run: Stream[F, Unit] =
        Stream
          .eval {
            F.delay(println(s"Waiting for messages on socket: ${multicastSettings.group}:${multicastSettings.port}"))
          }
          .flatMap { _ =>
            multicastSocket.socket.reads.evalMap { packet =>
              for {
                datagramJson    <- F.delay(new String(packet.bytes.toArray, StandardCharsets.UTF_8))
                datagramWrapper <- datagramUnrapper.unwrap(datagramJson)
                json            <- dataEncryptor.decrypt(datagramWrapper.payload)
                stockTick       <- messageTransformer.transform(json = json)
                output          <- messageFormatter.format(stockTick = stockTick)
                _               <- F.delay(println("------"))
                _               <- F.delay(println(output))
              } yield ()
            }.drain
          }
    }
}
