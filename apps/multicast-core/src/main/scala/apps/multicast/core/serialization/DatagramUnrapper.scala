package apps
package multicast
package core
package serialization

import cats.effect.{ Async, Resource }
import model.multicast.DatagramWrapper

sealed trait DatagramUnrapper[F[_]] {

  def unwrap(json: String): F[DatagramWrapper]
}

case object DatagramUnrapper {
  def create[F[_]](implicit F: Async[F]): Resource[F, DatagramUnrapper[F]] =
    Resource.eval {
      F.delay {
        new DatagramUnrapper[F] {
          override def unwrap(json: String): F[DatagramWrapper] =
            F.async_ { cb =>
              cb(DatagramWrapper.fromJson(json))
            }
        }
      }
    }
}
