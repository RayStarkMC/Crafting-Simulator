package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get

import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.util.UUID

private[recipe] case class SelectRecipeInputRecord(id: UUID, name: String, count: Long)
private[recipe] def selectRecipeInput(id: UUID): ConnectionIO[List[SelectRecipeInputRecord]] =
  sql"""
    select
      item.id,
      item.name,
      recipe_input.count
    from
      recipe_input
      join item on item.id = recipe_input.item_id
    where
      recipe_input.id = $id
    order by
      item.name,
      item.id
  """
    .query[SelectRecipeInputRecord]
    .to[List]