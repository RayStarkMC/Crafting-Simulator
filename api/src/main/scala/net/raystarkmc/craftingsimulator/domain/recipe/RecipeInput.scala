package net.raystarkmc.craftingsimulator.domain.recipe

import io.github.iltotore.iron.*

type RecipeInput = RecipeInput.T
object RecipeInput
  extends RefinedType[Seq[ItemWithCount], Pure]