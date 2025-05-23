package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe

import cats.*
import cats.data.*
import cats.effect.*
import cats.instances.all.given
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.lib.cats.*
import net.raystarkmc.craftingsimulator.lib.domain.*

trait PGRecipeRepository:
  given RecipeRepository[ConnectionIO]:
    def resolveById(recipeId: RecipeId): ConnectionIO[Option[Recipe]] =
      def restoreRecipe[G[_]: ApplicativeThrow as G](
        recipeRecord: SelectRecipeOutput,
        recipeInputRecords: Seq[SelectRecipeInputRecord],
        recipeOutputRecords: Seq[SelectRecipeOutputRecord],
      ): G[Recipe] =
        G.fromEither:
          (
            RecipeId(recipeRecord.id).pure[EitherNec[String, _]],
            ModelName
              .inParallel[EitherNec[ModelName.Failure, _]](recipeRecord.name)
              .leftMap:
                _.map:
                  _.show
              .map(RecipeName.apply),
            recipeInputRecords
              .traverse: record =>
                (
                  ItemId(record.itemId).pure[EitherNec[String, _]],
                  ItemCount
                    .inParallel[EitherNec[ItemCount.Failure, _]](record.count)
                    .leftMap:
                      _.map:
                        _.show,
                ).parMapN(ItemWithCount.apply)
              .map(RecipeInput.apply),
            recipeOutputRecords
              .traverse: record =>
                (
                  ItemId(record.itemId).pure[EitherNec[String, _]],
                  ItemCount
                    .inParallel[EitherNec[ItemCount.Failure, _]](record.count)
                    .leftMap:
                      _.map:
                        _.show,
                ).parMapN(ItemWithCount.apply)
              .map(RecipeOutput.apply),
          )
            .parMapN(Recipe.restore)
            .leftMap(_.show)
            .leftMap(new IllegalStateException(_))
      end restoreRecipe

      val optionT =
        for
          recipeRecord <- OptionT(selectRecipe(recipeId.value))
          recipeInputRecords <- OptionT.liftF(selectRecipeInput(recipeId.value))
          recipeOutputRecords <- OptionT.liftF(selectRecipeOutput(recipeId.value))

          recipe <- OptionT.liftF:
            restoreRecipe[ConnectionIO](
              recipeRecord,
              recipeInputRecords,
              recipeOutputRecords,
            )
        yield recipe
      optionT.value

    def save(recipe: Recipe): ConnectionIO[Unit] =
      for
        _ <- deleteRecipeInput(recipe.id.value)
        _ <- deleteRecipeOutput(recipe.id.value)
        _ <- upsertRecipe(recipe.id.value, recipe.name.value.value)
        _ <- insertRecipeOutputs(
          recipe.output.value.map: recipeOutput =>
            InsertRecipeOutputsRecord(
              recipeId = recipe.id.value,
              itemId = recipeOutput.item.value,
              count = recipeOutput.count.value,
            )
        )
        _ <- insertRecipeInputs(
          recipe.input.value.map: recipeInput =>
            InsertRecipeInputsRecord(
              recipeId = recipe.id.value,
              itemId = recipeInput.item.value,
              count = recipeInput.count.value,
            )
        )
      yield ()

    def delete(recipe: Recipe): ConnectionIO[Unit] =
      for
        _ <- deleteRecipeInput(recipe.id.value)
        _ <- deleteRecipeOutput(recipe.id.value)
        _ <- deleteRecipe(recipe.id.value)
      yield ()
  end given

object PGRecipeRepository extends PGRecipeRepository
