package net.raystarkmc.craftingsimulator

import cats.*
import cats.effect.*
import cats.instances.all.given
import cats.syntax.all.*
import com.comcast.ip4s.*
import net.raystarkmc.craftingsimulator.port.api.http4s.routes.allRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.ErrorHandling
import scala.concurrent.duration.*

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp:
        ErrorHandling.httpApp:
          allRoutes[IO].orNotFound
      .withShutdownTimeout(1.second)
      .build
      .useForever
