package net.raystarkmc.craftingsimulator.domain.recipe

import net.raystarkmc.craftingsimulator.lib.domain.{ModelName, ModelNameTypeOps}

type RecipeName = ModelName[RecipeContext]
object RecipeName extends ModelNameTypeOps[RecipeContext]
