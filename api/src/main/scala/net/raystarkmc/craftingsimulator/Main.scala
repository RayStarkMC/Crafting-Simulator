package net.raystarkmc.craftingsimulator

import cats.effect.*
import cats.syntax.foldable.given
import cats.{Applicative, Endo}
import com.comcast.ip4s.{ipv4, port}
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{CORS, ErrorAction, Logger}
import org.http4s.syntax.all.given
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.*

def requestLoggingMiddleware[F[_] : Async](app: HttpApp[F]): HttpApp[F] = {
  Logger.httpApp(
    logHeaders = true,
    logBody = true
  )(app)
}

def errorLoggingMiddleware[F[_] : Sync](app: HttpApp[F]): HttpApp[F] = {
  val logger = Slf4jLogger.getLogger[F]

  ErrorAction.httpApp.log(
    httpApp = app,
    messageFailureLogAction = logger.error(_)(_),
    serviceErrorLogAction = logger.error(_)(_)
  )
}

def corsSettingMiddleware[F[_] : Applicative](app: HttpApp[F]): HttpApp[F] = {
  CORS.policy.withAllowOriginAll(app)
}

def defineService(app: HttpApp[IO])(middlewares: Endo[HttpApp[IO]]*): HttpApp[IO] = {
  middlewares.foldK.apply(app)
}

val service = defineService(app)(
  corsSettingMiddleware, // for debug
  requestLoggingMiddleware, // for debug
  errorLoggingMiddleware
)

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(service)
      .withShutdownTimeout(1.second)
      .build
      .useForever
      .as(ExitCode.Success)
  }
}