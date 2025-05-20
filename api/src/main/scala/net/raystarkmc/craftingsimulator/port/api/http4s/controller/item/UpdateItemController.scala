package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.effect.Concurrent
import cats.effect.kernel.Async
import cats.instances.all.given
import cats.syntax.all.*
import io.circe.generic.auto.given
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.UpdateItemController.RequestBody
import net.raystarkmc.craftingsimulator.usecase.command.item.UpdateItemCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.item.UpdateItemCommandHandler.Failure
import org.http4s.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait UpdateItemController[F[_]]:
  def route: HttpRoutes[F]

object UpdateItemController:
  case class RequestBody(name: String)

  given [F[_]: {UpdateItemCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => UpdateItemController[F]:
    import dsl.*
    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case req @ PUT -> Root / "api" / "items" / UUIDVar(itemId) =>
          val eitherT =
            for
              body <- EitherT.right[Failure]:
                req.as[RequestBody]
              command = UpdateItemCommandHandler.Command(
                id = itemId,
                name = body.name,
              )
              _ <- EitherT:
                handler.run(command)
              response <- EitherT.right[Failure]:
                Ok()
            yield response

          eitherT.valueOrF:
            case Failure.ValidationFailed(detail) => BadRequest()
            case Failure.NotFound                 => NotFound()
