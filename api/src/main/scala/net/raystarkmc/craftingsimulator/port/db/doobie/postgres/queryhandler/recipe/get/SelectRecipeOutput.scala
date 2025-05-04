package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get

import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.util.UUID

private[recipe] case class SelectRecipeOutputRecord(id: UUID, name: String, count: Long)
private[recipe] def selectRecipeOutput(id: UUID): ConnectionIO[List[SelectRecipeOutputRecord]] =
  sql"""
    select
      item.id,
      item.name,
      recipe_output.count
    from
      recipe_output
      join item on item.id = recipe_output.item_id
    where
      recipe_output.id = $id
    order by
      item.name,
      item.id
  """
    .query[SelectRecipeOutputRecord]
    .to[List]