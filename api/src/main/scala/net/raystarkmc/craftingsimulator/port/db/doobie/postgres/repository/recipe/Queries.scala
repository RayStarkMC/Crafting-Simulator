package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe

import cats.implicits.*
import doobie.{ConnectionIO, Read}
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.util.UUID

private[recipe] case class SelectRecipeOutput(id: UUID, name: String)
    derives Read
private[recipe] def selectRecipe(
    recipeId: UUID
): ConnectionIO[Option[SelectRecipeOutput]] =
  sql"""
    select
      recipe.id,
      recipe.name
    from
      recipe
    where
      id = $recipeId
  """.query[SelectRecipeOutput].option

private[recipe] case class SelectRecipeInputRecord(itemId: UUID, count: Long)
    derives Read
private[recipe] def selectRecipeInput(
    recipeId: UUID
): ConnectionIO[List[SelectRecipeInputRecord]] =
  sql"""
    select
      recipe_input.item_id,
      recipe_input.count,
    from
      recipe_input
    where
      recipe_input.id = $recipeId
  """.query[SelectRecipeInputRecord].to[List]

private[recipe] case class SelectRecipeOutputRecord(
    recipeId: UUID,
    itemId: UUID,
    count: Long
) derives Read
private[recipe] def selectRecipeOutput(
    recipeId: UUID
): ConnectionIO[List[SelectRecipeOutputRecord]] =
  sql"""
    select
      recipe_output.item_id,
      recipe_output.count,
    from
      recipe_output
    where
      recipe_output.id = $recipeId
  """.query[SelectRecipeOutputRecord].to[List]

private[recipe] def deleteRecipeInput(recipeId: UUID): ConnectionIO[Unit] =
  sql"""
  delete
  from recipe_input
  where
    recipe_input.recipe_id = $recipeId
  """.update.run.void

private[recipe] def deleteRecipeOutput(recipeId: UUID): ConnectionIO[Unit] =
  sql"""
  delete
  from recipe_output
  where
    recipe_output.recipe_id = $recipeId
  """.update.run.void

private[recipe] def deleteRecipe(recipeId: UUID): ConnectionIO[Unit] =
  sql"""
    delete
    from recipe
    where
      recipe.id = $recipeId
  """.update.run.void