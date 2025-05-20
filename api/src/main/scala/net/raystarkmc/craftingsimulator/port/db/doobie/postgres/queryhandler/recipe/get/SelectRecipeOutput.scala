package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get

import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.util.UUID

private[get] case class SelectRecipeOutputRecord(id: UUID, name: Option[String], count: Long)
private[get] def selectRecipeOutput(recipeId: UUID): ConnectionIO[List[SelectRecipeOutputRecord]] =
  sql"""
    select
      recipe_output.item_id,
      item.name,
      recipe_output.count
    from
      recipe_output
      left join item on item.id = recipe_output.item_id
    where
      recipe_output.recipe_id = $recipeId
    order by
      item.name,
      item.id
  """
    .query[SelectRecipeOutputRecord]
    .to[List]
