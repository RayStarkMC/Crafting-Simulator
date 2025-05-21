package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.effect.Concurrent
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.usecase.command.item.DeleteItemCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.item.DeleteItemCommandHandler.Failure
import org.http4s.*
import org.http4s.HttpRoutes
import org.http4s.dsl.*

trait DeleteItemController[F[_]]:
  def route: HttpRoutes[F]

object DeleteItemController:
  given [F[_]: {DeleteItemCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => DeleteItemController[F]:
    import dsl.*
    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case req @ DELETE -> Root / "api" / "items" / UUIDVar(itemId) =>
          val command = DeleteItemCommandHandler.Command(
            id = itemId
          )
          val eitherT = for
            _ <- EitherT:
              handler.run(command)
            res <- EitherT.right[Failure]:
              Ok()
          yield res

          eitherT.valueOrF:
            case Failure.ModelNotFound => NotFound()
