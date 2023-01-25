package model.alphavantage

case class DataPoint(
  timestamp: String,
  open: String,
  high: String,
  low: String,
  close: String,
  volume: String
)

case class DataResponse(symbol: String, curve: List[DataPoint])
