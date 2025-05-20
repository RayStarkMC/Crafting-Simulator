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
import net.raystarkmc.craftingsimulator.usecase.command.recipe.DeleteRecipeCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.recipe.DeleteRecipeCommandHandler.*
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait DeleteRecipeController[F[_]]:
  def route: HttpRoutes[F]

object DeleteRecipeController:
  given [F[_]: {DeleteRecipeCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => DeleteRecipeController[F]:
    import dsl.*

    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case DELETE -> Root / "api" / "recipes" / UUIDVar(id) =>
          val command = Command(id = id)
          val eitherT =
            for
              _ <- EitherT:
                handler.run(command)
              response <- EitherT.right[Failure]:
                Ok()
            yield response
          eitherT.valueOrF:
            case Failure.ModelNotFound => NotFound()
