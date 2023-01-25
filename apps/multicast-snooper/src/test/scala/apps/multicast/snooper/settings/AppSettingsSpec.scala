package apps
package multicast
package snooper
package settings

import apps.multicast.snooper.settings.AppSettings.Settings
import cats.effect.IO
import com.comcast.ip4s.*
import model.multicast.MulticastSettings
import munit.*

class AppSettingsSpec extends CatsEffectSuite {

  test("load default config") {

    val loaded = AppSettings.load[IO].use {
      IO(_)
    }

    val expected = AppSettings(
      settings = Settings(
        multicast = MulticastSettings(
          group = mip"239.10.10.10",
          port = port"5555",
          interfacePrefix = "en"
        )
      )
    )

    loaded.assertEquals(expected)
  }
}
