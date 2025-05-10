package net.raystarkmc.craftingsimulator.usecase.query.recipe

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.*
import java.util.UUID
import net.raystarkmc.craftingsimulator.usecase.query.recipe.SearchRecipesQueryHandler.*

trait SearchRecipesQueryHandler[F[_]]:
  def run(input: Input): F[Recipes]

object SearchRecipesQueryHandler:
  case class Input(
    name: Option[String],
    inputIds: Option[NonEmptySet[UUID]],
    outputIds: Option[NonEmptySet[UUID]],
  )
  case class Recipes(recipes: List[Recipe])
  case class Recipe(id: UUID, name: String, inputCount: Long, outputCount: Long)
