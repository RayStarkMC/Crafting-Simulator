package net.raystarkmc.craftingsimulator.domain.recipe

import cats.{Hash, Show}
import net.raystarkmc.craftingsimulator.lib.domain.{ModelIdUUID, ModelIdUUIDTypeOps}

type RecipeId = ModelIdUUID[RecipeContext]
object RecipeId extends ModelIdUUIDTypeOps[RecipeContext]:
  given Hash[RecipeId] = hash
  given Show[RecipeId] = show