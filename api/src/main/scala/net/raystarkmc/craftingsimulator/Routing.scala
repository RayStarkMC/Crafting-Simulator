package net.raystarkmc.craftingsimulator

import cats.effect.{IO, Sync}
import cats.syntax.all.given
import doobie.ConnectionIO
import io.circe.syntax.given
import net.raystarkmc.craftingsimulator.domain.item.{ItemId, ItemRepository}
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.server.middleware.{ErrorAction, ErrorHandling}
import org.http4s.{HttpRoutes, Request, Response}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

given logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

val app: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "api" / "items" => postItems(req)
}

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

def postItems(req: Request[IO]): IO[Response[IO]] =
  import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.PGItemRepository.given

  val commandHandler = summon[RegisterItemCommandHandler[IO]]

  val command = RegisterItemCommandHandler.Command(
    name = "sample_item"
  )

  for {
    a <- commandHandler.run(command)
    res <- Ok(a.toString.asJson)
  } yield res
