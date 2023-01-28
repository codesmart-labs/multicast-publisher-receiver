package common.human.readable

import munit.FunSuite

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class HumanReadableDurationConvertorSpec extends FunSuite {
  import HumanReadableDurationConvertor.*

  def checkShow(name: String, original: FiniteDuration, expected: String)(implicit loc: munit.Location): Unit =
    test(name) {
      assertEquals(original.toHumanReadableDuration, expected)
    }


  checkShow("should print in milliseconds", FiniteDuration(999, TimeUnit.MILLISECONDS) , "999.0 ms")
  checkShow("should print in seconds", FiniteDuration(1000, TimeUnit.MILLISECONDS) , "1.0 s")
  checkShow("should print in minutes", FiniteDuration(60, TimeUnit.SECONDS) , "1.0 min")
  checkShow("should print in hours", FiniteDuration(60, TimeUnit.MINUTES) , "1.0 h")
  checkShow("should print in days", FiniteDuration(24, TimeUnit.HOURS) , "1.0 d")
}
