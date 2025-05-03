package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.*
import cats.effect.Concurrent
import cats.effect.kernel.Async
import io.circe.generic.auto.given
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.UpdateItemController.RequestBody
import net.raystarkmc.craftingsimulator.usecase.command.UpdateItemCommandHandler
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait UpdateItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object UpdateItemController extends UpdateItemControllerGivens:
  case class RequestBody(name: String)

trait UpdateItemControllerGivens:
  given [F[_]: {UpdateItemCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => UpdateItemController[F]:
    import dsl.*

    def run: PartialFunction[Request[F], F[Response[F]]] = {
      case req @ PUT -> Root / "api" / "items" / UUIDVar(itemId) =>
        val eitherT = for {
          body <- EitherT.right[UpdateItemCommandHandler.Failure] {
            req.as[RequestBody]
          }
          command = UpdateItemCommandHandler.Command(
            id = itemId,
            name = body.name
          )
          _ <- EitherT(handler.run(command))
          response <- EitherT.right[UpdateItemCommandHandler.Failure](Ok())
        } yield response

        eitherT.valueOrF {
          case UpdateItemCommandHandler.Failure.ValidationFailed(detail) => BadRequest()
          case UpdateItemCommandHandler.Failure.NotFound => NotFound()
        }
    }
