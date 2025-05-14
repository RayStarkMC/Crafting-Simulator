package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.search

import cats.*
import cats.data.*
import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.fragments
import java.util.UUID
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.CSFragments.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.CSFragments.exists
import net.raystarkmc.craftingsimulator.usecase.query.recipe.SearchRecipesQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.recipe.SearchRecipesQueryHandler.*

trait PGSearchRecipeQueryHandler:
  given [F[_]] => (T: Transaction[ConnectionIO, F]) => SearchRecipesQueryHandler[F]:
    def run(input: Input): F[Recipes] =
      case class SelectOutputRecord(
        id: UUID,
        name: String,
        inputCount: Long,
        outputCount: Long,
      )
      val selectRecipes =
        fr"""
          with
            recipe_ids_filtered as
        """
        ++
        fragments.parentheses:
          fr"""
            select
              recipe.id as id
            from
              recipe
          """ ++ fragments.whereAndOpt(
            input.name.map: recipeName =>
              fr"""recipe.name LIKE '%' || $recipeName || '%'""",
            input.inputIds.map: inputs =>
              exists:
                fr"""
                  SELECT 1 FROM recipe_input as recipe_input_e
                  WHERE
                    recipe_input_e.recipe_id = recipe.id and
                """ ++ fragments.in(fr"""recipe_input_e.item_id""", inputs)
            ,
            input.outputIds.map: outputs =>
              exists:
                fr"""
                  SELECT 1 FROM recipe_output as recipe_output_e
                  WHERE
                    recipe_output_e.recipe_id = recipe.id and
                """ ++ fragments.in(fr"""recipe_output_e.item_id""", outputs),
          )
        ++ fr"""
          select
            recipe.id,
            recipe.name,
            recipe_input_count.value as input_count,
            recipe_output_count.value as output_count
          from
            recipe
            left outer join lateral (
              select
                count(recipe_input.item_id) as value
              from recipe_input
              where
                recipe.id = recipe_input.recipe_id
            ) as recipe_input_count on true
            left outer join lateral (
              select
                count(recipe_output.item_id) as value
              from recipe_output
              where
                recipe.id = recipe_output.recipe_id
            ) as recipe_output_count on true
          where
            EXISTS (select 1 from recipe_ids_filtered where recipe_ids_filtered.id = recipe.id)
        """

      val runQuery = selectRecipes.query[SelectOutputRecord].to[List]
      T.withTransaction:
        for
          records <- runQuery
          recipes = Recipes:
            records.map: record =>
              Recipe(
                id = record.id,
                name = record.name,
                inputCount = record.inputCount,
                outputCount = record.outputCount,
              )
        yield recipes
    end run

object PGSearchRecipeQueryHandler extends PGSearchRecipeQueryHandler
