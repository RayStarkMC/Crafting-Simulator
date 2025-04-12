package net.raystarkmc.craftingsimulator.domain.recipe

import net.raystarkmc.craftingsimulator.lib.domain.{ModelIdUUID, ModelIdUUIDTypeOps}

type RecipeId = ModelIdUUID[RecipeContext]
object RecipeId extends ModelIdUUIDTypeOps[RecipeContext]