package net.raystarkmc.craftingsimulator

import cats.*
import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import net.raystarkmc.craftingsimulator.port.api.http4s.Routing
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGSearchItemsQueryHandler.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGGetItemQueryHandler.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.PGItemRepository.given
import net.raystarkmc.craftingsimulator.usecase.command.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.ErrorHandling

import scala.concurrent.duration.*

object Main extends IOApp:
  private val routing = summon[Routing[IO]]

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(ErrorHandling.httpApp(routing.routes.orNotFound))
      .withShutdownTimeout(1.second)
      .build
      .useForever
      .as(ExitCode.Success)
