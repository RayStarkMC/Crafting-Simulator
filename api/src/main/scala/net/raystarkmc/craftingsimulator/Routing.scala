package net.raystarkmc.craftingsimulator

import cats.data.OptionT
import cats.effect.{IO, Sync}
import cats.syntax.all.given
import io.circe.generic.auto.given
import io.circe.syntax.given
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.server.middleware.{ErrorAction, ErrorHandling}
import org.http4s.{HttpRoutes, Request, Response}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

given logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

case class Hello(message: String)

val app: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "api" / "hello" => hello(req)
  case req @ GET -> Root / "api" / "error" => throw new RuntimeException("wow!")
}

def errorHandler[F[_]: Sync : Logger](t: Throwable, msg: => String): F[Unit] = {
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

def hello(req: Request[IO]) = {
  for {
    body <- req.decodeJson[Hello]
    res <- Ok(body.asJson)
  } yield {
    res
  }
}


