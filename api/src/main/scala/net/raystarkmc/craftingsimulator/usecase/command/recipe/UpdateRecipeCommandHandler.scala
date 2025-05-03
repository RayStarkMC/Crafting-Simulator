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

import java.util.UUID

trait UpdateRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Either[Failure, Unit]]

object UpdateRecipeCommandHandler:
  case class Command(id: UUID, name: String) derives Hash, Show
  enum Failure derives Hash, Show:
    case ValidationFailed(detail: String)
    case NotFound

  given [F[_]: {Monad, UUIDGen, RecipeRepository as recipeRepository}] => UpdateRecipeCommandHandler[F]:
    def run(command: Command): F[Either[Failure, Unit]] =
      val recipeId = RecipeId(command.id)
      val eitherT: EitherT[F, Failure, Unit] = for {
        name <- ModelName
          .inParallel[EitherNec[ModelName.Failure, _]](command.name)
          .leftMap(_.show)
          .map(RecipeName.apply)
          .leftMap(Failure.ValidationFailed.apply)
          .toEitherT[F]
        recipe <- EitherT.fromOptionF(
          recipeRepository.resolveById(recipeId),
          Failure.NotFound
        )
        updated = recipe.update(name)
        _ <- EitherT.right[Failure](
          recipeRepository.save(updated)
        )
      } yield ()
      eitherT.value
