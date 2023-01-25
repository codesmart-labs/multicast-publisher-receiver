package apps
package multicast
package publisher
package stream

import cats.effect.std.AtomicCell
import cats.effect.{ Async, Resource }
import cats.implicits.*
import fs2.*
import model.alphavantage.StockTick
import multicast.publisher.process.{ DataFlattner, RequestProcessor }
import multicast.publisher.settings.AppSettings
import multicast.publisher.settings.AppSettings.FUNCTION_TIME_SERIES_INTRADAY
import org.http4s.Uri
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

sealed trait StockTickSource[F[_]] {

  def stream: Stream[F, StockTick]

}
case object StockTickSource {

  def create[F[_]](appSettings: AppSettings, httpClient: Client[F])(implicit
    F: Async[F],
    logger: Logger[F]
  ): Resource[F, StockTickSource[F]] =
    Resource.eval {
      F.delay {
        new StockTickSource[F] {

          def buildRequestUri(baseUri: Uri, queryParams: Map[String, String], symbol: String): Uri =
            baseUri
              .withQueryParam("symbol", symbol)
              .withQueryParams(queryParams)

          override def stream: Stream[F, StockTick] =
            for {
              tickInterval     <- Stream.emit(appSettings.settings.stream.tickInterval)
              symbols          <- Stream.emit(appSettings.settings.alphavantage.symbols)
              interDayRequest  <- Stream.emit(appSettings.settings.alphavantage.functions(FUNCTION_TIME_SERIES_INTRADAY))
              queryParams      <- Stream.emit(interDayRequest.queryParams)
              limitEntries     <- Stream.emit(interDayRequest.limitEntries)
              requestProcessor <-
                Stream.eval(RequestProcessor.create(httpClient = httpClient, limitEntries = limitEntries))
              atomicCounter    <- Stream.eval(AtomicCell[F].of[Long](0L))
              dataFlattner     <- Stream.eval(DataFlattner.create(counter = atomicCounter))
              result           <- Stream
                                    .fixedRateStartImmediately(tickInterval)
                                    .evalMap { time =>
                                      logger.info(s"At time: $time, the stream has been running for: $time.")
                                    }
                                    .flatMap { _ =>
                                      Stream
                                        .iterable(symbols)
                                        .map { symbol =>
                                          Stream
                                            .emit(symbol)
                                            .evalMap { symbol =>
                                              F.delay {
                                                (
                                                  symbol,
                                                  buildRequestUri(
                                                    baseUri = interDayRequest.baseUri,
                                                    queryParams = queryParams,
                                                    symbol = symbol
                                                  )
                                                )
                                              }
                                            }
                                            .evalTap { case (symbol, uri) =>
                                              for {
                                                _ <- logger.info(s"Processing symbol: $symbol")
                                                _ <- logger.info(s"Using Uri: $uri")
                                              } yield ()

                                            }
                                            .evalMap { case (symbol, uri) =>
                                              requestProcessor.process(symbol, uri)
                                            }
                                            .flatMap {
                                              dataFlattner.flatten
                                            }
                                        }
                                        .parJoinUnbounded
                                    }
                                    .evalMap { stockTick =>
                                      for {
                                        _ <- logger.info(s"Created: $stockTick")
                                      } yield stockTick
                                    }
            } yield result
        }
      }
    }
}
