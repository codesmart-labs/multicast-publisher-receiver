package apps
package multicast
package publisher
package stream

import cats.effect.*
import cats.effect.std.PQueue
import cats.implicits.*
import fs2.*
import model.alphavantage.StockTick
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.*

sealed trait ReprioritizeFlow[F[_]] {

  def reprioritize: Stream[F, StockTick]

}
case object ReprioritizeFlow {

  def create[F[_]](flow: Stream[F, StockTick], tickDelay: FiniteDuration)(implicit
    F: Async[F],
    logger: Logger[F]
  ): Resource[F, ReprioritizeFlow[F]] =
    for {
      queue <- Resource.eval {
                 PQueue.unbounded[F, StockTick]
               }
    } yield new ReprioritizeFlow[F] {
      override def reprioritize: Stream[F, StockTick] = {
        val pusher = flow
          .evalMap { stockTick =>
            for {
              size <- queue.size
              _    <- logger.info(s"Pushing: $stockTick, at size: $size")
            } yield stockTick
          }
          .evalMap(queue.offer)

        val popper = Stream
          .fixedRate(tickDelay)
          .evalMap(_ => queue.take)
          .evalMap { stockTick =>
            for {
              size <- queue.size
              _    <- logger.info(s"Popped: $stockTick, remaining size: $size")
            } yield stockTick
          }

        popper
          .concurrently(pusher)
      }
    }
}
