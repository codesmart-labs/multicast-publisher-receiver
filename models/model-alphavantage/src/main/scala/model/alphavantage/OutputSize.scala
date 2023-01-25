package model.alphavantage

import cats.Show

sealed trait OutputSize

case object OutputSize {

  implicit val showOutputSize: Show[OutputSize] = Show.show(_.getClass.getSimpleName.toLowerCase().stripSuffix("$"))

  case object Compact extends OutputSize

  case object Full extends OutputSize

  def fromString(value: String): OutputSize =
    value.toLowerCase() match {
      case "compact" => Compact
      case "full"    => Full
      case _         => throw InvalidOutputSizeException(value)

    }

  case class InvalidOutputSizeException(value: String)
      extends RuntimeException(s"Value $value is not a valid output size")
}
