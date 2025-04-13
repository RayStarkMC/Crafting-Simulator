package net.raystarkmc.craftingsimulator

import cats.*
import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGGetItemQueryHandler.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGSearchItemsQueryHandler.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.item.PGItemRepository.given
import net.raystarkmc.craftingsimulator.usecase.command.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.ErrorHandling
import org.http4s.{HttpRoutes, Response}

import scala.concurrent.duration.*

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(
        ErrorHandling.httpApp(
          HttpRoutes
            .of[IO](
              PartialFunction.empty
                orElse summon[RegisterItemController[IO]].run
                orElse summon[SearchItemsController[IO]].run
                orElse summon[GetItemController[IO]].run
                orElse summon[UpdateItemController[IO]].run
                orElse summon[DeleteItemController[IO]].run
            )
            .orNotFound
        )
      )
      .withShutdownTimeout(1.second)
      .build
      .useForever
      .as(ExitCode.Success)
