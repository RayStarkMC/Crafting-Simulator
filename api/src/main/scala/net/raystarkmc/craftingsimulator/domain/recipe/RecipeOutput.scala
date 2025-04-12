package net.raystarkmc.craftingsimulator.domain.recipe

import io.github.iltotore.iron.*

opaque type RecipeOutput = Seq[ItemWithCount] :| Pure
object RecipeOutput
    extends RefinedTypeOps[Seq[ItemWithCount], Pure, RecipeOutput]
