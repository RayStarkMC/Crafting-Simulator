package net.raystarkmc.craftingsimulator.port.api.http4s.controller.recipe

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.*
import cats.instances.all.given
import cats.syntax.all.*
import io.circe.generic.auto.given
import io.circe.syntax.given
import java.util.UUID
import net.raystarkmc.craftingsimulator.usecase.command.recipe.UpdateRecipeCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.recipe.UpdateRecipeCommandHandler.*
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait UpdateRecipeController[F[_]]:
  def route: HttpRoutes[F]

object UpdateRecipeController:
  case class RequestBody(
    name: String
  )

  case class ItemWithCount(
    itemID: UUID,
    count: Long,
  )

  given [F[_]: {UpdateRecipeCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => UpdateRecipeController[F]:
    import dsl.*

    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case req @ PUT -> Root / "api" / "recipes" / UUIDVar(id) =>
          val eitherT =
            for
              body <- EitherT.right[Failure]:
                req.as[RequestBody]
              command = Command(
                id = id,
                name = body.name,
              )
              _ <- EitherT:
                handler.run(command)
              response <- EitherT.right[Failure]:
                Ok()
            yield response
          eitherT.valueOrF:
            case Failure.ValidationFailed(detail) => BadRequest(detail.show)
