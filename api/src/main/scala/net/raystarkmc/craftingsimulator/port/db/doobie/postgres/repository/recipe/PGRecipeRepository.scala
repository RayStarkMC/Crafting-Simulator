package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe

import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import io.github.iltotore.iron.*
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe.PGRecipeRepository.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa

import java.util.UUID

trait PGRecipeRepository[F[_]: Async] extends RecipeRepository[F]:
  override def resolveById(recipeId: RecipeId): F[Option[Recipe]] =
    def restoreRecipe[G[_]: ApplicativeThrow](
        recipeRecord: SelectRecipeOutput,
        recipeInputRecords: List[SelectRecipeInputRecord],
        recipeOutputRecords: List[SelectRecipeOutputRecord]
    ): G[Recipe] =
      (
        RecipeId(recipeRecord.id).pure[G],
        RecipeName
          .ae[[A] =>> ValidatedNec[RecipeName.Failure, A]](recipeRecord.name)
          .leftMap(a => new IllegalStateException(a.show))
          .fold[G[RecipeName]](_.raiseError, _.pure),
        recipeInputRecords
          .traverse { record =>
            (
              ItemId(record.itemId).pure[G],
              ItemCount
                .ae[[A] =>> ValidatedNec[ItemCount.Failure, A]](record.count)
                .leftMap(a => new IllegalStateException(a.show))
                .fold[G[ItemCount]](_.raiseError, _.pure)
            ).mapN(ItemWithCount.apply)
          }
          .map(RecipeInput.apply),
        recipeOutputRecords
          .traverse { record =>
            (
              ItemId(record.itemId).pure[G],
              ItemCount
                .ae[[A] =>> ValidatedNec[ItemCount.Failure, A]](record.count)
                .leftMap(a => new IllegalStateException(a.show))
                .fold[G[ItemCount]](_.raiseError, _.pure)
            ).mapN(ItemWithCount.apply)
          }
          .map(RecipeOutput.apply)
      ).mapN(Recipe.restore)

    val transactionT = for {
      recipeRecord <- OptionT(selectRecipe(recipeId.value))
      recipeInputRecords <- OptionT.liftF(selectRecipeInput(recipeId.value))
      recipeOutputRecords <- OptionT.liftF(selectRecipeOutput(recipeId.value))

      recipe <- OptionT.liftF {
        restoreRecipe[ConnectionIO](
          recipeRecord,
          recipeInputRecords,
          recipeOutputRecords
        )
      }
    } yield {
      recipe
    }

    transactionT.value.transact[F](xa)

  override def save(recipe: Recipe): F[Unit] =
    val transaction =
      for {
        _ <- deleteRecipeInput(recipe.id.value)
        _ <- deleteRecipeOutput(recipe.id.value)
        _ <- upsertRecipe(recipe.id.value, recipe.name.value)
        _ <- insertRecipeOutputs(
          recipe.output.toList.map { recipeOutput =>
            InsertRecipeOutputsRecord(
              recipeId = recipe.id.value,
              itemId = recipeOutput.item.value,
              count = recipeOutput.count.value
            )
          }
        )
        _ <- insertRecipeInputs(
          recipe.input.toList.map { recipeInput =>
            InsertRecipeInputsRecord(
              recipeId = recipe.id.value,
              itemId = recipeInput.item.value,
              count = recipeInput.count.value
            )
          }
        )
      } yield ()

    transaction.transact[F](xa)

  override def delete(recipe: Recipe): F[Unit] =
    val transaction = for {
      _ <- deleteRecipeInput(recipe.id.value)
      _ <- deleteRecipeOutput(recipe.id.value)
      _ <- deleteRecipe(recipe.id.value)
    } yield ()

    transaction.transact[F](xa)

object PGRecipeRepository:
  given [F[_]: Async] => RecipeRepository[F] =
    object repository extends PGRecipeRepository
    repository
