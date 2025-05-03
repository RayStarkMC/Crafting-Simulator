package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.command.recipe.DeleteRecipeCommandHandler.*

import java.util.UUID

trait DeleteRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Either[Failure, Unit]]

object DeleteRecipeCommandHandler:
  case class Command(id: UUID) derives Eq, Hash, Order, Show
  enum Failure derives Eq, Hash, Order, Show:
    case ModelNotFound

  given [
      F[_],
      G[_]: {RecipeRepository as recipeRepository, Monad}
  ] => (T: Transaction[G, F]) => DeleteRecipeCommandHandler[F]:
    def run(command: Command): F[Either[Failure, Unit]] =
      val recipeId = RecipeId(command.id)
      val eitherT = T.withTransaction {
        for {
          recipe <- EitherT.fromOptionF(
            recipeRepository.resolveById(recipeId),
            Failure.ModelNotFound
          )
          _ <- EitherT.right[Failure] {
            recipeRepository.delete(recipe)
          }
        } yield ()
      }
      eitherT.value
