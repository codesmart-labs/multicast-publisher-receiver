package apps
package multicast
package publisher
package process

import cats.effect.Async
import cats.effect.std.AtomicCell
import cats.implicits.*
import model.alphavantage.{ DataPoint, DataResponse, StockTick }
import fs2.*

sealed trait DataFlattner[F[_]] {
  def flatten(data: DataResponse): Stream[F, StockTick]
}

case object DataFlattner {
  def create[F[_]](counter: AtomicCell[F, Long])(implicit F: Async[F]): F[DataFlattner[F]] =
    F.delay {
      new DataFlattner[F] {
        override def flatten(data: DataResponse): Stream[F, StockTick] =
          Stream
            .iterable(data.curve)
            .evalMap { case DataPoint(timestamp, open, high, low, close, volume) =>
              for {
                c <- counter.getAndUpdate(_ + 1)
              } yield StockTick(
                id = c,
                symbol = data.symbol,
                timestamp = timestamp,
                open = open,
                high = high,
                low = low,
                close = close,
                volume = volume
              )
            }
      }
    }
}
