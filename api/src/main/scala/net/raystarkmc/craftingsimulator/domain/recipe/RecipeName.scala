package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName

type RecipeName = RecipeName.T
object RecipeName extends ModelName[RecipeContext]:
  given Hash[RecipeName] = hash
  given Show[RecipeName] = show
