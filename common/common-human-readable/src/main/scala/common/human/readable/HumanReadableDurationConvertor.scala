package common.human.readable

import squants.time.*

import scala.concurrent.duration.FiniteDuration

object HumanReadableDurationConvertor {

  implicit class ApplyFiniteDuration(val duration: FiniteDuration) extends AnyVal {
    def toHumanReadableDuration: String = {
      duration.toMillis match {
        case d if d >= 1000 * 60 * 60 * 24 => Days(d / (1000 * 60 * 60* 24))
        case d if d >= 1000 * 60 * 60 => Hours(d / (1000 * 60 * 60))
        case d if d >= 1000 * 60 => Minutes(d / (1000 * 60))
        case d if d >= 1000 => Seconds(d / 1000)
        case d => Milliseconds(d)
      }
    }.toString()
  }
}
