package apps
package multicast
package snooper
package message

import cats.effect.{ Async, Resource }
import model.alphavantage.StockTick

sealed trait MessageTransformer[F[_]] {

  def transform(json: String): F[StockTick]
}

case object MessageTransformer {

  def create[F[_]](implicit F: Async[F]): Resource[F, MessageTransformer[F]] =
    Resource.eval {
      F.delay {
        new MessageTransformer[F] {
          override def transform(json: String): F[StockTick] =
            F.async_ { cb =>
              cb(StockTick.fromJson(json))
            }
        }
      }
    }
}
