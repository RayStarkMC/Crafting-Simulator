package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get

import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.util.UUID

private[get] case class SelectRecipeRecord(name: String)
private[get] def selectRecipe(id: UUID): ConnectionIO[Option[SelectRecipeRecord]] =
  sql"""
    select
      item.name
    from
      item
    where
      item.id = $id
    order by
      item.name,
      item.id
  """
    .query[SelectRecipeRecord]
    .option
