package net.raystarkmc.craftingsimulator.port.api.http4s.controller

import cats.effect.Concurrent
import cats.syntax.all.given
import io.circe.syntax.given
import net.raystarkmc.craftingsimulator.usecase.command.DeleteItemCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.DeleteItemCommandHandler.Command
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.given

trait DeleteItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object DeleteItemController extends DeleteItemControllerGivens

trait DeleteItemControllerGivens:
  given [F[_]: {Concurrent, DeleteItemCommandHandler}]
    => DeleteItemController[F] =
    object instance extends DeleteItemController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[DeleteItemCommandHandler[F]]

      def run: PartialFunction[Request[F], F[Response[F]]] = {
        case req @ DELETE -> Root / "api" / "items" / UUIDVar(itemId) =>
          val command = Command(
            id = itemId
          )
          for {
            _ <- handler.run(command)
            res <- Ok()
          } yield res
      }
    instance
