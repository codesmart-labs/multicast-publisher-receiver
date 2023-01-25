package model.alphavantage

import cats.implicits.catsSyntaxOrder
import cats.{ Order, Show }

case class StockTick(
  id: Long,
  symbol: String,
  timestamp: String,
  open: String,
  high: String,
  low: String,
  close: String,
  volume: String
)

case object StockTick {
  implicit val showStockTick: Show[StockTick] = Show.show(instance => instance.toString)

  implicit val defaultOrder: Order[StockTick] =
    Order.from[StockTick]((a, b) => (a.id, a.timestamp, a.symbol).compare((b.id, b.timestamp, b.symbol)))

  def toJson(s: StockTick): String = {
    import io.circe.generic.auto.*
    import io.circe.syntax.*
    s.asJson.noSpaces
  }

  def fromJson(json: String): Either[io.circe.Error, StockTick] = {
    import io.circe.generic.auto.*
    import io.circe.parser.decode

    decode[StockTick](json)
  }
}
