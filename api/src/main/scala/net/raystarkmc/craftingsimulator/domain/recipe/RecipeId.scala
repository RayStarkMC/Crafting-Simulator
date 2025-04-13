package net.raystarkmc.craftingsimulator.domain.recipe

import cats.{Hash, Show}
import net.raystarkmc.craftingsimulator.lib.domain.*

type RecipeId = RecipeId.T
object RecipeId extends ModelIdUUID[RecipeContext]:
  given Hash[RecipeId] = hash
  given Show[RecipeId] = show