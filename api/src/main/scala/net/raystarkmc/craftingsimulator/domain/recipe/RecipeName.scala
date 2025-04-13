package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import net.raystarkmc.craftingsimulator.lib.domain.{ModelName, ModelNameTypeOps}

type RecipeName = ModelName[RecipeContext]
object RecipeName extends ModelNameTypeOps[RecipeContext]:
  given Hash[RecipeName] = hash
  given Show[RecipeName] = show
