package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get

import java.util.UUID
import net.raystarkmc.craftingsimulator.usecase.query.recipe.GetRecipeQueryHandler.*

private[get] def buildRecipe(
  recipeID: UUID,
  recipeRecord: SelectRecipeRecord,
  recipeInputRecords: List[SelectRecipeInputRecord],
  recipeOutputRecords: List[SelectRecipeOutputRecord],
): Recipe = Recipe(
  id = recipeID,
  name = recipeRecord.name,
  input = recipeInputRecords
    .map: record =>
      record.id -> ItemNameWithCount(
        record.name,
        record.count,
      )
    .toMap,
  output = recipeOutputRecords
    .map: record =>
      record.id -> ItemNameWithCount(
        record.name,
        record.count,
      )
    .toMap,
)
