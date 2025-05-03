package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName
import net.raystarkmc.craftingsimulator.usecase.command.recipe.UpdateRecipeCommandHandler.*
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction

import java.util.UUID

trait UpdateRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Either[Failure, Unit]]

object UpdateRecipeCommandHandler:
  case class Command(id: UUID, name: String) derives Eq, Hash, Order, Show
  enum Failure derives Eq, Hash, Order, Show:
    case ValidationFailed(detail: String)
    case ModelNotFound

  given [
      F[_]: Monad,
      G[_]: {RecipeRepository as recipeRepository, Monad}
  ] => (T: Transaction[G, F]) => UpdateRecipeCommandHandler[F]:
    def run(command: Command): F[Either[Failure, Unit]] =
      val recipeId = RecipeId(command.id)
      val eitherT: EitherT[F, Failure, Unit] = for {
        name <- ModelName
          .inParallel[EitherNec[ModelName.Failure, _]](command.name)
          .leftMap(_.show)
          .map(RecipeName.apply)
          .leftMap(Failure.ValidationFailed.apply)
          .toEitherT[F]
        _ <- T.withTransaction {
          for {
            recipe <- EitherT.fromOptionF(
              recipeRepository.resolveById(recipeId),
              Failure.ModelNotFound
            )
            updated = recipe.update(name)
            _ <- EitherT.right[Failure](
              recipeRepository.save(updated)
            )
          } yield ()
        }
      } yield ()
      eitherT.value
