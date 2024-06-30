package net.raystarkmc.craftingsimulator

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import io.circe.*
import io.circe.generic.auto.given
import io.circe.syntax.given
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.syntax.all.given

import scala.concurrent.duration.*

case class Hello(message: String)

val routes = HttpRoutes.of[IO] {
  case req @ POST -> Root / "hello" =>
    for {
      body <- req.decodeJson[Hello]
      res <- Ok(body.asJson)
    } yield {
      res
    }
}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .withShutdownTimeout(1.second)
      .build
      .useForever
      .as(ExitCode.Success)
  }
}