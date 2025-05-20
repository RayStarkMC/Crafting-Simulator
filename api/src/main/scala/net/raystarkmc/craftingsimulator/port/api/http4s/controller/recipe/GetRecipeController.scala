package net.raystarkmc.craftingsimulator.port.api.http4s.controller.recipe

import cats.*
import cats.data.*
import cats.effect.*
import cats.syntax.all.*
import io.circe.generic.auto.given
import io.circe.syntax.given
import java.util.UUID
import net.raystarkmc.craftingsimulator.usecase.query.recipe.GetRecipeQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.recipe.GetRecipeQueryHandler.*
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.dsl.*

trait GetRecipeController[F[_]]:
  def route: HttpRoutes[F]

object GetRecipeController:
  case class ResponseBody(
    id: UUID,
    name: String,
    input: List[Item],
    output: List[Item],
  )

  case class Item(
    id: UUID,
    name: Option[String],
    count: Long,
  )

  given [F[_]: {GetRecipeQueryHandler as getRecipeQueryHandlerF, Http4sDsl as dsl, Concurrent}]
    => GetRecipeController[F]:
    import dsl.*
    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case GET -> Root / "api" / "recipes" / UUIDVar(recipeId) =>
          val input = Input(recipeId)
          val eitherT = for
            recipe <- EitherT:
              getRecipeQueryHandlerF.run(input)
            responseBody = ResponseBody(
              id = recipe.id,
              name = recipe.name,
              input = recipe.input.map: i =>
                Item(
                  id = i.id,
                  name = i.name,
                  count = i.count,
                ),
              output = recipe.output.map: i =>
                Item(
                  id = i.id,
                  name = i.name,
                  count = i.count,
                ),
            )
            response <- EitherT.right[Failure]:
              Ok(responseBody.asJson)
          yield response
          eitherT.valueOrF:
            case Failure.ModelNotFound => NotFound()
