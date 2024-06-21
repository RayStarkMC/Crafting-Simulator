package net.raystarkmc.craftingsimulator

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.`Content-Type`

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS
import scala.concurrent.duration.Duration

val routes = HttpRoutes.of[IO] {
  case GET -> Root / "hello" => Ok("Hello World!", `Content-Type`(MediaType.application.json))
}

object Main extends IOApp.Simple {
  def run: IO[Unit] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .withShutdownTimeout(Duration(1, SECONDS))
      .build
      .useForever
      .as(ExitCode.Success)
  }
}