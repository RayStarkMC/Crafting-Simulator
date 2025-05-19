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
import net.raystarkmc.craftingsimulator.usecase.query.recipe.SearchRecipesQueryHandler
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait SearchRecipesController[F[_]]:
  def route: HttpRoutes[F]

object SearchRecipesController:
  case class RequestBody(
    name: Option[String],
    inputsFilter: Option[NonEmptyList[UUID]],
    outputsFilter: Option[NonEmptyList[UUID]],
  )

  case class ResponseBody(recipes: List[Recipe])
  case class Recipe(id: UUID, name: String, inputCount: Long, outputCount: Long)

  given [F[_]: {SearchRecipesQueryHandler as searchRecipesQueryHandlerF, Http4sDsl as dsl, Concurrent}]
    => SearchRecipesController[F]:
    import dsl.*

    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case req @ POST -> Root / "api" / "search" / "recipes" =>
          for
            body <- req.as[RequestBody]
            input = SearchRecipesQueryHandler.Input(
              name = body.name,
              inputIds = body.inputsFilter.map:
                _.toNes
              ,
              outputIds = body.outputsFilter.map:
                _.toNes,
            )
            recipes <- searchRecipesQueryHandlerF.run(input)
            responseBody = ResponseBody(
              recipes = recipes.recipes.map: recipe =>
                Recipe(
                  id = recipe.id,
                  name = recipe.name,
                  inputCount = recipe.inputCount,
                  outputCount = recipe.outputCount,
                )
            )
            response <- Ok(responseBody.asJson)
          yield response
