package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get

import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import java.util.UUID

private[get] case class SelectRecipeInputRecord(id: UUID, name: Option[String], count: Long)
private[get] def selectRecipeInput(recipeId: UUID): ConnectionIO[List[SelectRecipeInputRecord]] =
  sql"""
    select
      recipe_input.item_id,
      item.name,
      recipe_input.count
    from
      recipe_input
      left join item on item.id = recipe_input.item_id
    where
      recipe_input.recipe_id = $recipeId
    order by
      item.name,
      item.id
  """
    .query[SelectRecipeInputRecord]
    .to[List]
