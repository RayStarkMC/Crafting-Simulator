package net.raystarkmc.craftingsimulator

import cats.*
import cats.effect.*
import cats.instances.all.given
import cats.syntax.all.*
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.recipe.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.instances.given
import org.http4s.HttpRoutes
import org.http4s.Response
import org.http4s.dsl.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.ErrorHandling
import scala.concurrent.duration.*

given [F[_]] => Http4sDsl[F] = Http4sDsl[F]

def allRoutes[
  F[_]: {Monad, RegisterItemController as RegisterItemController, SearchItemsController as SearchItemsController,
    GetItemController as GetItemController, UpdateItemController as UpdateItemController,
    DeleteItemController as DeleteItemController, RegisterRecipeController as RegisterRecipeController}
]: HttpRoutes[F] =
  HttpRoutes.empty[F]
    <+> RegisterRecipeController.route
    <+> HttpRoutes.of[F](
      PartialFunction.empty
        orElse RegisterItemController.run
        orElse SearchItemsController.run
        orElse GetItemController.run
        orElse UpdateItemController.run
        orElse DeleteItemController.run
    )

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
