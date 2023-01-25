package apps
package multicast
package publisher
package process

import cats.effect.{ Async, Clock, Resource }
import cats.implicits.*
import fs2.*
import model.alphavantage.StockTick
import model.multicast.DatagramWrapper
import multicast.core.transit.DataEncryptor

import java.net.InetAddress

sealed trait DatagramWrappingEngine[F[_]] {
  def wrap: Pipe[F, StockTick, DatagramWrapper]
}

case object DatagramWrappingEngine {
  def create[F[_]](dataEncryptor: DataEncryptor[F])(implicit F: Async[F]): Resource[F, DatagramWrappingEngine[F]] =
    Resource.pure {
      new DatagramWrappingEngine[F] {
        override def wrap: Pipe[F, StockTick, DatagramWrapper] =
          _.evalMap { stockTick =>
            for {
              ip              <- F.delay(InetAddress.getLocalHost.toString)
              timestamp       <- Clock[F].realTime.map(_.toMillis)
              stockTickJson   <- F.delay(StockTick.toJson(stockTick))
              payload         <- dataEncryptor.encrypt(stockTickJson)
              datagramWrapper <- F.delay {
                                   DatagramWrapper(
                                     id = stockTick.id,
                                     publisher = ip,
                                     timestamp = timestamp,
                                     payload = payload
                                   )
                                 }
            } yield datagramWrapper
          }
      }
    }
}
