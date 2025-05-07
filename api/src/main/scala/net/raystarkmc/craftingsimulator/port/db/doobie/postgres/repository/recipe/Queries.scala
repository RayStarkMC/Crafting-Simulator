package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe

import cats.data.NonEmptyList
import cats.instances.all.given
import cats.syntax.all.*
import doobie.*
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
): ConnectionIO[Seq[SelectRecipeInputRecord]] =
  sql"""
    select
      recipe_input.item_id,
      recipe_input.count
    from
      recipe_input
    where
      recipe_input.recipe_id = $recipeId
  """.query[SelectRecipeInputRecord].to[Seq]

private[recipe] case class SelectRecipeOutputRecord(
    recipeId: UUID,
    itemId: UUID,
    count: Long
) derives Read
private[recipe] def selectRecipeOutput(
    recipeId: UUID
): ConnectionIO[Seq[SelectRecipeOutputRecord]] =
  sql"""
    select
      recipe_output.item_id,
      recipe_output.count
    from
      recipe_output
    where
      recipe_output.recipe_id = $recipeId
  """.query[SelectRecipeOutputRecord].to[Seq]

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

private[recipe] def upsertRecipe(
    recipeId: UUID,
    recipeName: String
): ConnectionIO[Unit] =
  sql"""
    insert into recipe (id, name, created_at, updated_at)
    values (
      $recipeId,
      $recipeName,
      current_timestamp,
      current_timestamp
    )
    on conflict (id) do
    update set
      name = excluded.name,
      updated_at = current_timestamp
  """.update.run.void

private[recipe] case class InsertRecipeOutputsRecord(
    recipeId: UUID,
    itemId: UUID,
    count: Long
)
private[recipe] def insertRecipeOutputs(
    records: Seq[InsertRecipeOutputsRecord]
): ConnectionIO[Unit] = {
  val option = for {
    values <- records.toList.toNel.map(Fragments.values)
    insertSql =
      fr"""
        insert into recipe_output (recipe_id, item_id, count)
      """ +~+ values
  } yield {
    insertSql
  }
  option.traverse_(_.update.run)
}

private[recipe] case class InsertRecipeInputsRecord(
    recipeId: UUID,
    itemId: UUID,
    count: Long
)
private[recipe] def insertRecipeInputs(
    records: Seq[InsertRecipeInputsRecord]
): ConnectionIO[Unit] = {
  val option = for {
    values <- records.toList.toNel.map(Fragments.values)
    insertSql =
      fr"""
        insert into recipe_input (recipe_id, item_id, count)
      """ +~+ values
  } yield {
    insertSql
  }
  option.traverse_(_.update.run)
}
