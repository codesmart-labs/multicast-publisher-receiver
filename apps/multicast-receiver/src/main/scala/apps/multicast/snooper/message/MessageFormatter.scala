package apps
package multicast
package snooper
package message

import cats.effect.{ Async, Clock, Resource }
import cats.implicits.*
import common.pretty.cli.ConsoleColorise.*
import common.time.TimeConverter.*
import model.alphavantage.StockTick

sealed trait MessageFormatter[F[_]] {

  def format(stockTick: StockTick): F[String]
}

case object MessageFormatter {

  def create[F[_]](implicit F: Async[F]): Resource[F, MessageFormatter[F]] =
    Resource.eval {
      F.delay {
        new MessageFormatter[F] {
          override def format(stockTick: StockTick): F[String] =
            for {
              timestamp <- Clock[F].realTime.map(_.toMillis.format(DATE_FORMAT_ISO8601))
              output    <- F.delay {
                             s"""| C: ${stockTick.id} | T: $timestamp
                              | SYM: ${stockTick.symbol.blue.bold} | HIGH: ${stockTick.high.green.bold} | LOW: ${stockTick.low.red.bold} | AT: ${stockTick.timestamp.bold}
                              |""".stripMargin
                           }
            } yield output
        }
      }
    }
}
