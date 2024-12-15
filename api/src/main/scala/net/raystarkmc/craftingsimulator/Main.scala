package net.raystarkmc.craftingsimulator

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder

import scala.concurrent.duration.*

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(withErrorLogging.orNotFound)
      .withShutdownTimeout(1.second)
      .build
      .useForever
      .as(ExitCode.Success)
  }
}