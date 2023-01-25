package model.multicast

import com.comcast.ip4s.{ IpAddress, Multicast, Port }

case class MulticastSettings(
  group: Multicast[IpAddress],
  port: Port,
  interfacePrefix: String
)
