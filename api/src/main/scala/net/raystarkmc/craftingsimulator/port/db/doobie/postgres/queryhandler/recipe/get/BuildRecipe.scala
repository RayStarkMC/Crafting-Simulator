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
  input = recipeInputRecords.map: record =>
    Item(
      id = record.id,
      name = record.name,
      count = record.count,
    ),
  output = recipeOutputRecords.map: record =>
    Item(
      id = record.id,
      name = record.name,
      count = record.count,
    ),
)
