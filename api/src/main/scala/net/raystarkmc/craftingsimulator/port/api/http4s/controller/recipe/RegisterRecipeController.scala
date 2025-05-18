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
import net.raystarkmc.craftingsimulator.usecase.command.recipe.RegisterRecipeCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.recipe.RegisterRecipeCommandHandler.*
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait RegisterRecipeController[F[_]]:
  def route: HttpRoutes[F]

object RegisterRecipeController:
  case class RequestBody(
    name: String,
    inputs: List[ItemWithCount],
    outputs: List[ItemWithCount],
  )

  case class ItemWithCount(
    itemID: UUID,
    count: Long,
  )

  case class ReponseBody(id: UUID)

  given [F[_]: {RegisterRecipeCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => RegisterRecipeController[F]:
    import dsl.*

    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case req @ POST -> Root / "api" / "recipes" =>
          val eitherT =
            for
              body <- EitherT.right[Failure]:
                req.as[RequestBody]
              command = Command(
                name = body.name,
                inputs = body.inputs.map: it =>
                  (it.itemID, it.count),
                outputs = body.outputs.map: it =>
                  (it.itemID, it.count),
              )
              output <- EitherT:
                handler.run(command)
              responseBody = ReponseBody(
                id = output.id
              )
              response <- EitherT.right[Failure]:
                Ok(responseBody.asJson)
            yield response
          eitherT.valueOrF:
            case Failure.ValidationFailed(detail) => BadRequest(detail.show)
