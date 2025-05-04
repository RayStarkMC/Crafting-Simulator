package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get

import cats.*
import cats.data.*
import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import java.util.UUID
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.query.recipe.GetRecipeQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.recipe.GetRecipeQueryHandler.*

trait PGGetRecipeQueryHandler:
  given [F[_]] => (T: Transaction[ConnectionIO, F]) => GetRecipeQueryHandler[F]:
    def run(input: Input): F[Either[Failure, Recipe]] =
      val recipeID = input.id
      val eitherT = T.withTransaction:
        for {
          recipeRecord <- EitherT.fromOptionF(
            selectRecipe(recipeID),
            Failure.ModelNotFound,
          )
          recipeInputRecords <- EitherT.right[Failure]:
            selectRecipeInput(recipeID)
          recipeOutputRecords <- EitherT.right[Failure]:
            selectRecipeOutput(recipeID)
          recipe = buildRecipe(
            recipeID = recipeID,
            recipeRecord = recipeRecord,
            recipeInputRecords = recipeInputRecords,
            recipeOutputRecords = recipeOutputRecords,
          )
        } yield recipe
      eitherT.value
