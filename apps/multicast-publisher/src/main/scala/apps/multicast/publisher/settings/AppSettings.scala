package apps
package multicast
package publisher
package settings

import cats.effect.{ Resource, Sync }
import com.comcast.ip4s.*
import AppSettings.Settings
import model.authentication.Platform
import model.multicast.MulticastSettings
import org.http4s.Uri
import pureconfig.*
import pureconfig.generic.auto.*
import pureconfig.module.catseffect.syntax.*
import pureconfig.module.http4s.*
import pureconfig.module.ip4s.*

import scala.concurrent.duration.FiniteDuration

case class AppSettings(settings: Settings) {}

object AppSettings {
  val FUNCTION_TIME_SERIES_INTRADAY = "TIME_SERIES_INTRADAY"

  implicit val multicastAddressReader: ConfigReader[Multicast[IpAddress]] = ConfigReader[String].map { ip =>
    IpAddress
      .fromString(ip)
      .flatMap(_.asMulticast)
      .get
  }
//  implicit val requestIntervalReader: ConfigReader[RequestInterval] = ConfigReader[Int].map(RequestInterval.fromInt)
//  implicit val outputSizeConvert: ConfigReader[OutputSize]          = deriveEnumerationReader[OutputSize]
  case class Function(
    baseUri: Uri,
    queryParams: Map[String, String],
    limitEntries: Option[Int]
  )

  case class Alphavantage(
    functions: Map[String, Function],
    symbols: List[String]
  )

  case class StreamSettings(tickInterval: FiniteDuration, tickDelay: FiniteDuration)

  case class Encryption(enabled: Boolean, platform: Platform)
  case class Settings(
    stream: StreamSettings,
    alphavantage: Alphavantage,
    multicast: MulticastSettings,
    encryption: Encryption
  )

  def load[F[_]](implicit F: Sync[F]): Resource[F, AppSettings] =
    Resource.eval {
      ConfigSource.default.loadF[F, AppSettings]()
    }
}
