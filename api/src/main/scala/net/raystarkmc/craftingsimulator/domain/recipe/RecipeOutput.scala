package net.raystarkmc.craftingsimulator.domain.recipe

import io.github.iltotore.iron.*

type RecipeOutput = RecipeOutput.T
object RecipeOutput
    extends RefinedType[Seq[ItemWithCount], Pure]
