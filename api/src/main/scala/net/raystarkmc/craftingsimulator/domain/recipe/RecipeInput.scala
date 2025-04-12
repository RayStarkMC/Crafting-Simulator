package net.raystarkmc.craftingsimulator.domain.recipe

import io.github.iltotore.iron.*

opaque type RecipeInput = Seq[ItemWithCount] :| Pure
object RecipeInput
  extends RefinedTypeOps[Seq[ItemWithCount], Pure, RecipeInput]