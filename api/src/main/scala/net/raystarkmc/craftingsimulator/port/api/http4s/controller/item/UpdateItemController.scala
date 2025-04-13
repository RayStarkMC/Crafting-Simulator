package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.effect.Concurrent
import cats.effect.kernel.Async
import cats.syntax.all.given
import io.circe.generic.auto.given
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.UpdateItemController.RequestBody
import net.raystarkmc.craftingsimulator.usecase.command.UpdateItemCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.UpdateItemCommandHandler.Command
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.given

trait UpdateItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object UpdateItemController extends UpdateItemControllerGivens:
  case class RequestBody(name: String)

trait UpdateItemControllerGivens:
  given [F[_]: {Concurrent, UpdateItemCommandHandler}]
    => UpdateItemController[F] =
    object instance extends UpdateItemController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[UpdateItemCommandHandler[F]]

      def run: PartialFunction[Request[F], F[Response[F]]] = {
        case req @ PUT -> Root / "api" / "items" / UUIDVar(itemId) =>
          for {
            body <- req.as[RequestBody]
            command = Command(
              id = itemId,
              name = body.name
            )
            result <- handler.run(command)
            res <- result.fold(
              error =>
                error match {
                  case UpdateItemCommandHandler.Error.NameError(detail) =>
                    BadRequest()
                  case UpdateItemCommandHandler.Error.NotFound => NotFound()
                },
              _ => Ok()
            )
          } yield res
      }
    instance
