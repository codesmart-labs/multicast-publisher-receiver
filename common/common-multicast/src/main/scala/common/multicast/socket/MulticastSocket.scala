package common
package multicast
package socket

import cats.effect.{ Async, Resource }
import cats.implicits.*
import com.comcast.ip4s.MulticastJoin
import fs2.io.net.{ DatagramSocket, DatagramSocketOption, Network }
import model.multicast.MulticastSettings

import java.net.{ NetworkInterface, StandardProtocolFamily }

sealed trait MulticastSocket[F[_]] {
  def socket: DatagramSocket[F]

}

case object MulticastSocket {

  def create[F[_]](multicastSettings: MulticastSettings)(implicit F: Async[F]): Resource[F, MulticastSocket[F]] =
    for {
      v4ProtocolFamily <- Resource.pure(StandardProtocolFamily.INET)
      v4Interfaces     <- Resource.pure {
                            import scala.jdk.CollectionConverters.*
                            NetworkInterface.getNetworkInterfaces.asScala
                              .to(List)
                              .filter(_.getName.startsWith(multicastSettings.interfacePrefix))
                          }
      groupJoin        <- Resource.pure(MulticastJoin.asm(multicastSettings.group))
      datagramSocket   <- Network[F].openDatagramSocket(
                            port = Some(multicastSettings.port),
                            options = List(
                              DatagramSocketOption.reuseAddress(true),
                              DatagramSocketOption.reusePort(true),
                              DatagramSocketOption.multicastInterface(v4Interfaces.head)
                            ),
                            protocolFamily = Some(v4ProtocolFamily)
                          )
      _                <- Resource.eval {
                            v4Interfaces.traverse { interface =>
                              datagramSocket.join(groupJoin, interface)
                            }
                          }
    } yield new MulticastSocket[F] {
      override def socket: DatagramSocket[F] = datagramSocket
    }
}
