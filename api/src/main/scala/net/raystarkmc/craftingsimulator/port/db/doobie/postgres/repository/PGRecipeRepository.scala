package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository

import cats.*
import cats.data.*
import cats.effect.*
import cats.syntax.all.*
import cats.implicits.given
import doobie.*
import doobie.hi.*
import doobie.implicits.given
import doobie.postgres.*
import doobie.postgres.implicits.given
import io.github.iltotore.iron.*
import io.github.iltotore.iron.doobie.given
import net.raystarkmc.craftingsimulator.domain.item.ItemId.*
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeId.*
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeName.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa

import java.util.UUID

trait PGRecipeRepository[F[_]: Async] extends RecipeRepository[F]:
  private case class RecipeTableRecord(
      id: UUID,
      name: String
  )

  private case class RecipeInputRecord(
      recipeId: UUID,
      itemId: UUID,
      count: Long
  )

  private case class RecipeOutputRecord(
      recipeId: UUID,
      itemId: UUID,
      count: Long
  )

  override def resolveById(recipeId: RecipeId): F[Option[Recipe]] =
    val selectRecipe = sql"""
        select
          recipe.id,
          recipe.name
        from
          recipe
        where
          id = ${recipeId}
      """.query[RecipeTableRecord]

    val selectInput = sql"""
        select
          recipe_input.recipe_id,
          recipe_input.item_id,
          recipe_input.count,
        from
          recipe_input
        where
          id = ${recipeId}
         """.query[RecipeInputRecord]

    val selectOutput =
      sql"""
        select
          recipe_output.id,
          recipe_output.name
        from
          recipe_output
        where
          id = ${recipeId}
         """.query[RecipeOutputRecord]

    type G[A] = Either[NonEmptyChain[RecipeName.Failure], A]

    val optionT = for {
      recipeRecord <- OptionT(selectRecipe.option)
      recipeInputRecords <- OptionT.liftF(selectInput.to[Seq])
      recipeOutputRecords <- OptionT.liftF(selectOutput.to[Seq])

      recipeName <- OptionT.liftF[ConnectionIO, RecipeName] {
        RecipeName
          .ae[G](recipeRecord.name)
          .leftMap(a => new IllegalStateException(a.show))
          .fold(_.raiseError, _.pure)
      }
      recipe = Recipe.restore(
        id = RecipeId(recipeRecord.id),
        name = recipeName,
        inputs = RecipeInput(Seq.empty), //TODO 復元する
        outputs = RecipeOutput(Seq.empty)
      )
    } yield recipe

    optionT.value.transact[F](xa)

  override def save(recipe: Recipe): F[Unit] =
    val transaction =
      for {
        _ <- sql"""
          delete
          from recipe_input
          where
            recipe_input.recipe_id = ${recipe.id}
                """.update.run
        _ <- sql"""
          delete
          from recipe_output
          where
            recipe_output.recipe_id = ${recipe.id}
                """.update.run
        _ <- sql"""
          insert into recipe (id, name, created_at, updated_at)
          values (
            ${recipe.id},
            ${recipe.name},
            current_timestamp,
            current_timestamp
          )
          on conflict (id) do
          update set
            name = excluded.name,
            updated_at = current_timestamp
                """.update.run
        _ <- Update[(UUID, UUID, Long)](
          """
          insert into recipe_input (recipe_id, item_id, count, created_at, updated_at)
          values (?, ?, ?, current_timestamp, current_timestamp)
          """
        ).updateMany(recipe.input.value.map { itemWithCount =>
          (
            recipe.id,
            itemWithCount.item,
            itemWithCount.count
          )
        })
        _ <- Update[(UUID, UUID, Long)](
          """
          insert into recipe_output (recipe_id, item_id, count, created_at, updated_at)
          values (?, ?, ?, current_timestamp, current_timestamp)
          """
        ).updateMany(recipe.output.value.map { itemWithCount =>
          (
            recipe.id,
            itemWithCount.item,
            itemWithCount.count
          )
        })
      } yield ()

    transaction.transact[F](xa)

  override def delete(recipe: Recipe): F[Unit] =
    val transaction = for {
      _ <- sql"""
        delete
        from recipe_input
        where
          recipe_input.recipe_id = ${recipe.id}
            """.update.run
      _ <- sql"""
        delete
        from recipe_output
        where
          recipe_output.recipe_id = ${recipe.id}
            """.update.run
      _ <- sql"""
        delete
        from recipe
        where
          recipe.id = ${recipe.id}
              """.update.run
    } yield ()

    transaction.transact[F](xa)

object PGRecipeRepository:
  given [F[_]: Async] => RecipeRepository[F] =
    object repository extends PGRecipeRepository
    repository
