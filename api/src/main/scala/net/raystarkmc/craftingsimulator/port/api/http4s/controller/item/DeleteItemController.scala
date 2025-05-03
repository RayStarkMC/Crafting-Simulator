package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.syntax.all.*
import cats.instances.all.given
import cats.effect.Concurrent
import net.raystarkmc.craftingsimulator.usecase.command.DeleteItemCommandHandler
import org.http4s.*
import org.http4s.dsl.*

trait DeleteItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object DeleteItemController extends DeleteItemControllerGivens

trait DeleteItemControllerGivens:
  given [F[_]: {Concurrent, DeleteItemCommandHandler as handler}] => DeleteItemController[F]:
    private val dsl = Http4sDsl[F]
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
