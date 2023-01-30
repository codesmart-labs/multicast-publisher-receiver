package apps
package multicast
package publisher

import apps.multicast.core.transit.DataEncryptor
import apps.multicast.publisher.process.DatagramWrappingEngine
import apps.multicast.publisher.settings.AppSettings
import apps.multicast.publisher.stream.{ MulticastSink, ReprioritizeFlow, StockTickSource }
import cats.effect.std.UUIDGen
import cats.effect.{ Async, Resource }
import cats.implicits.*
import fs2.*
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.Logger

sealed trait Orchestrator[F[_]] {

  def run: Stream[F, Unit]
}
case object Orchestrator {
  def create[F[_]](implicit F: Async[F], logger: Logger[F]): Resource[F, Orchestrator[F]] =
    for {
      appSettings            <- AppSettings.load
      tickDelay              <- Resource.pure(appSettings.settings.stream.tickDelay)
      publisherId            <- Resource.eval(UUIDGen.randomString)
      httpClient             <- EmberClientBuilder.default[F].build
      stockTickSource        <- StockTickSource.create(appSettings = appSettings, httpClient = httpClient)
      dataEncryptor          <-
        DataEncryptor.create(appSettings.settings.encryption.enabled, appSettings.settings.encryption.platform)
      datagramWrappingEngine <- DatagramWrappingEngine.create(dataEncryptor = dataEncryptor, publisherId = publisherId)
      multicastSink          <-
        MulticastSink.create(multicastSettings = appSettings.settings.multicast)
      reorderFlow            <- ReprioritizeFlow.create(flow = stockTickSource.stream, tickDelay = tickDelay)
    } yield new Orchestrator[F] {
      override def run: Stream[F, Unit] =
        reorderFlow.reprioritize
          .through(datagramWrappingEngine.wrap)
          .through(multicastSink.publish)
          .evalMap { stockTick =>
            for {
              _ <- logger.info(s"Sunset: $stockTick")
            } yield ()
          }
    }
}
