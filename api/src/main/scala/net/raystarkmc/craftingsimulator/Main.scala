package net.raystarkmc.craftingsimulator

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import com.comcast.ip4s.{ipv4, port}
import net.raystarkmc.craftingsimulator.port.api.http4s.Routing
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.*
import net.raystarkmc.craftingsimulator.usecase.command.*
import net.raystarkmc.craftingsimulator.usecase.query.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.PGItemRepository.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGGetAllItemsQueryHandler.given
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{ErrorAction, ErrorHandling}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.*

object Main extends IOApp:
  private val routing = summon[Routing[IO]]

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(withErrorLogging(routing.routes).orNotFound)
      .withShutdownTimeout(1.second)
      .build
      .useForever
      .as(ExitCode.Success)

given logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

def errorHandler[F[_]: Sync: Logger](t: Throwable, msg: => String): F[Unit] =
  Slf4jLogger.create[F] >>= (_.error(t)(msg))

def withErrorLogging[F[_]: Sync: Logger](routes: HttpRoutes[F]): HttpRoutes[F] = ErrorHandling.Recover.total(
  ErrorAction.log(
    http = routes,
    messageFailureLogAction = errorHandler,
    serviceErrorLogAction = errorHandler
  )
)