package apps
package multicast
package publisher
package process

import cats.effect.Async
import fs2.*
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*
import model.alphavantage.{ DataPoint, DataResponse }
import org.http4s.client.Client
import org.http4s.{ Method, Request, Uri }
sealed trait RequestProcessor[F[_]] {
  def process(symbol: String, uri: Uri): F[DataResponse]
}
case object RequestProcessor        {
  def create[F[_]](httpClient: Client[F], limitEntries: Option[Int])(implicit F: Async[F]): F[RequestProcessor[F]] =
    F.delay {
      new RequestProcessor[F] {
        implicit val dataPointDecoder: CsvRowDecoder[DataPoint, String] = deriveCsvRowDecoder

        override def process(symbol: String, uri: Uri): F[DataResponse] = {
          for {
            request      <- Stream.emit(Request[F](Method.GET, uri))
            responseBody <- httpClient.stream(request).map(_.body.covary[F])
            dataPoints   <- buildDataPoints(responseBody)
          } yield DataResponse(symbol, dataPoints)
        }.compile.lastOrError

        private def buildDataPoints(responseBody: Stream[F, Byte]): Stream[F, List[DataPoint]] =
          limitEntries match {
            case None        =>
              Stream.eval {
                responseBody
                  .through(fs2.text.utf8.decode)
                  .through(decodeUsingHeaders[DataPoint]())
                  .compile
                  .toList
              }
            case Some(limit) =>
              Stream.eval {
                responseBody
                  .through(fs2.text.utf8.decode)
                  .through(decodeUsingHeaders[DataPoint]())
                  .take(limit)
                  .compile
                  .toList
              }
          }
      }
    }
}
