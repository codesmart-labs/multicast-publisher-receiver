package model.multicast

case class DatagramWrapper(
  id: Long,
  publisher: String,
  timestamp: Long,
  payload: String
)

case object DatagramWrapper {
  def toJson(s: DatagramWrapper): String = {
    import io.circe.generic.auto.*
    import io.circe.syntax.*
    s.asJson.noSpaces
  }

  def fromJson(json: String): Either[io.circe.Error, DatagramWrapper] = {
    import io.circe.generic.auto.*
    import io.circe.parser.decode
    decode[DatagramWrapper](json)
  }
}
