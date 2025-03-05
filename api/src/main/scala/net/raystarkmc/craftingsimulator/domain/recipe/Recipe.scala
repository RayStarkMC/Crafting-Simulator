package net.raystarkmc.craftingsimulator.domain.recipe

import net.raystarkmc.craftingsimulator.domain.item.{*, given}
import net.raystarkmc.craftingsimulator.lib.domain.*

private sealed trait RecipeContext

type RecipeId = ModelIdUUID[RecipeContext]
object RecipeId extends ModelIdUUIDTypeOps[RecipeContext]
export RecipeId.{*, given}

type RecipeName = ModelName[RecipeContext]
object RecipeName extends ModelNameTypeOps[RecipeContext]
export RecipeName.{*, given}

case class RecipeInput(
    items: Seq[ItemId]
)

case class RecipeOutput(
    items: Seq[ItemId]
)

case class Recipe(
    id: RecipeId,
    name: RecipeName
)
