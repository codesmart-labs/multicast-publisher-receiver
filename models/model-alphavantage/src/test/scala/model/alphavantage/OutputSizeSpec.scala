package model.alphavantage

import cats.implicits.*
import model.alphavantage.OutputSize.InvalidOutputSizeException
import munit.FunSuite

class OutputSizeSpec extends FunSuite {

  def checkShow(name: String, original: OutputSize, expected: String)(implicit loc: munit.Location): Unit =
    test(name) {
      assertEquals(original.show, expected)
    }

  def checkValidFromString(name: String, original: String, expected: OutputSize)(implicit loc: munit.Location): Unit =
    test(name) {
      assertEquals(OutputSize.fromString(original), expected)
    }

  def checkInvalidFromString(original: String, errorMessage: String)(implicit loc: munit.Location): Unit =
    interceptMessage[InvalidOutputSizeException](errorMessage) {
      OutputSize.fromString(original)
    }

  checkShow("should show full", OutputSize.Full, "full")
  checkShow("should show compact", OutputSize.Compact, "compact")

  checkValidFromString("should convert full to valid OutputSize", "full", OutputSize.Full)
  checkValidFromString("should convert compact to valid OutputSize", "compact", OutputSize.Compact)

  checkInvalidFromString("fullCompact", "Value fullCompact is not a valid output size")
  checkInvalidFromString("compactFill", "Value compactFill is not a valid output size")
}
