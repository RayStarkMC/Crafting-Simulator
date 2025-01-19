package net.raystarkmc.craftingsimulator

import cats.*
import cats.instances.all.given 
import cats.syntax.all.given 
import cats.effect.*
import cats.effect.instances.all.given 
import cats.effect.syntax.all.given 
import com.comcast.ip4s.{ipv4, port}
import net.raystarkmc.craftingsimulator.port.api.http4s.app
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{ErrorAction, ErrorHandling}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

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

given logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

def errorHandler[F[_]: Sync: Logger](t: Throwable, msg: => String): F[Unit] = {
  for {
    logger <- Slf4jLogger.create[F]
    _ <- logger.error(t)(msg)
  } yield {
    ()
  }
}

val withErrorLogging: HttpRoutes[IO] = ErrorHandling.Recover.total(
  ErrorAction.log(
    http = app,
    messageFailureLogAction = errorHandler,
    serviceErrorLogAction = errorHandler
  )
)