package model.alphavantage

import cats.implicits.*
import model.alphavantage.StockTick.*
import munit.FunSuite

import scala.io.Source

class StockTickSpec extends FunSuite {
  def checkShow(name: String, original: StockTick, expected: String)(implicit loc: munit.Location): Unit =
    test(name) {
      assertEquals(original.show, expected)
    }

  def checkToJson(name: String, original: StockTick, expected: String)(implicit loc: munit.Location): Unit =
    test(name) {
      assertEquals(toJson(original), expected)
    }

  def checkOrder(name: String, grater: StockTick, lesser: StockTick)(implicit loc: munit.Location): Unit =
    test(name) {
      assert(grater > lesser)
    }

  checkShow(
    "should show StockTick object",
    StockTick(
      id = 1,
      symbol = "IBM",
      timestamp = "2023-01-20 20:00:00",
      open = "141.4000",
      high = "141.4000",
      low = "141.4000",
      close = "141.4000",
      volume = "10"
    ),
    "StockTick(1,IBM,2023-01-20 20:00:00,141.4000,141.4000,141.4000,141.4000,10)"
  )
  checkToJson(
    "should convert StockTick to json",
    StockTick(
      id = 1,
      symbol = "IBM",
      timestamp = "2023-01-20 20:00:00",
      open = "141.4000",
      high = "141.4000",
      low = "141.4000",
      close = "141.4000",
      volume = "10"
    ),
    Source.fromResource("stock-tick.json").mkString
  )

  val a1 = StockTick(
    id = 1,
    symbol = "IBM",
    timestamp = "2023-01-20 20:00:00",
    open = "141.4000",
    high = "141.4000",
    low = "141.4000",
    close = "141.4000",
    volume = "10"
  )
  val a2 = a1.copy(timestamp = "2023-01-20 19:00:00")
  val b1 = a1.copy(symbol = "W")
  val b2 = b1.copy(timestamp = "2023-01-20 19:00:00")

  checkOrder("compare based on timestamp only", a1, a2)
  checkOrder("compare based on symbol only", b1, a1)
  checkOrder("compare based on symbol and timestamp", b1, a2)
}
