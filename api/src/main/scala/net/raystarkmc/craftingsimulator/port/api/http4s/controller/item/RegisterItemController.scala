package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.effect.*
import cats.syntax.all.given
import io.circe.generic.auto.given
import io.circe.syntax.given
import net.raystarkmc.craftingsimulator.usecase.command.item.RegisterItemCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.item.RegisterItemCommandHandler.Failure
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait RegisterItemController[F[_]]:
  def route: HttpRoutes[F]

object RegisterItemController:
  given [F[_]: {RegisterItemCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => RegisterItemController[F]:
    import dsl.*

    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case req @ POST -> Root / "api" / "items" =>
          val eitherT =
            for
              command <- EitherT.right[Failure]:
                req.as[RegisterItemCommandHandler.Command]
              RegisterItemCommandHandler.Output(id) <- EitherT:
                handler.run(command)
              res <- EitherT.right[Failure]:
                Ok(id.show)
            yield res
          eitherT.valueOrF:
            case Failure.ValidationFailed(detail) => BadRequest(detail)
