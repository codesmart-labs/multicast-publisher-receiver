package model
package alphavantage

import cats.Show

sealed trait RequestInterval {
  val value: Int
}

case object RequestInterval {

  implicit val showRequestInterval: Show[RequestInterval] = Show.show(interval => s"${interval.value}min")
  case object One     extends RequestInterval {
    override val value: Int = 1
  }
  case object Five    extends RequestInterval {
    override val value: Int = 5
  }
  case object Fifteen extends RequestInterval {
    override val value: Int = 15
  }
  case object Thirty  extends RequestInterval {
    override val value: Int = 30
  }
  case object Sixty   extends RequestInterval {
    override val value: Int = 60
  }

  def fromInt(value: Int): RequestInterval =
    value match {
      case 1  => One
      case 5  => Five
      case 15 => Fifteen
      case 30 => Thirty
      case 60 => Sixty
      case _  => throw InvalidRequestIntervalException(value)

    }

  case class InvalidRequestIntervalException(value: Int)
      extends RuntimeException(s"Value $value is not a valid request interval")
}
