package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.syntax.all.*
import cats.instances.all.given
import cats.effect.Concurrent
import net.raystarkmc.craftingsimulator.usecase.command.item.DeleteItemCommandHandler
import org.http4s.*
import org.http4s.dsl.*

trait DeleteItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object DeleteItemController:
  given [F[_]: {DeleteItemCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => DeleteItemController[F]:
    import dsl.*
    def run: PartialFunction[Request[F], F[Response[F]]] =
      case req @ DELETE -> Root / "api" / "items" / UUIDVar(itemId) =>
        val command = DeleteItemCommandHandler.Command(
          id = itemId
        )
        val eitherT = for {
          _ <- EitherT {
            handler.run(command)
          }
          res <- EitherT.right[DeleteItemCommandHandler.Failure] {
            Ok()
          }
        } yield res

        eitherT.valueOrF { case DeleteItemCommandHandler.Failure.ModelNotFound =>
          NotFound()
        }
