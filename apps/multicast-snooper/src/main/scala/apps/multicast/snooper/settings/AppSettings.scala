package apps
package multicast
package snooper
package settings

import cats.effect.{ Resource, Sync }
import com.comcast.ip4s.*
import model.multicast.MulticastSettings
import multicast.snooper.settings.AppSettings.Settings
import pureconfig.*
import pureconfig.generic.auto.*
import pureconfig.module.catseffect.syntax.*
import pureconfig.module.ip4s.*

case class AppSettings(settings: Settings) {}

object AppSettings {
  implicit val multicastAddressReader: ConfigReader[Multicast[IpAddress]] = ConfigReader[String].map { ip =>
    IpAddress
      .fromString(ip)
      .flatMap(_.asMulticast)
      .get
  }
  case class Settings(
    multicast: MulticastSettings
  )

  def load[F[_]](implicit F: Sync[F]): Resource[F, AppSettings] =
    Resource.eval {
      ConfigSource.default.loadF[F, AppSettings]()
    }
}
