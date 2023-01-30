package apps
package multicast
package receiver
package message

import cats.effect.{ Async, Resource }
import cats.implicits.*
import common.human.readable.HumanReadableDurationConvertor.*
import common.pretty.cli.ConsoleColorise.*
import model.alphavantage.StockTick

import scala.concurrent.duration.FiniteDuration

sealed trait MessageFormatter[F[_]] {

  def format(stockTick: StockTick, latency: FiniteDuration, publisherId: String): F[String]
}

case object MessageFormatter {

  def create[F[_]](implicit F: Async[F]): Resource[F, MessageFormatter[F]] =
    Resource.eval {
      F.delay {
        new MessageFormatter[F] {
          override def format(stockTick: StockTick, latency: FiniteDuration, publisherId: String): F[String] =
            for {
              lat    <- F.delay(latency.toHumanReadableDuration)
              output <- F.delay {
                          s"""| S: ${publisherId.cyan.bold} | C: ${stockTick.id} | L: ${lat.yellow.bold} |
                              | SYM: ${stockTick.symbol.blue.bold} | HIGH: ${stockTick.high.green.bold} | LOW: ${stockTick.low.red.bold} | AT: ${stockTick.timestamp.bold} |
                              |""".stripMargin
                        }
            } yield output
        }
      }
    }
}
