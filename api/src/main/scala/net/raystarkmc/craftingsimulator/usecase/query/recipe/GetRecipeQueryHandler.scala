package net.raystarkmc.craftingsimulator.usecase.query.recipe

import java.util.UUID
import net.raystarkmc.craftingsimulator.usecase.query.recipe.GetRecipeQueryHandler.*

trait GetRecipeQueryHandler[F[_]]:
  def run(input: Input): F[Either[Failure, Recipe]]

object GetRecipeQueryHandler:
  case class Input(id: UUID)
  enum Failure:
    case ModelNotFound
  case class Recipe(
    id: UUID,
    name: String,
    input: Map[UUID, ItemNameWithCount],
    output: Map[UUID, ItemNameWithCount],
  )
  case class ItemNameWithCount(name: String, count: Long)
