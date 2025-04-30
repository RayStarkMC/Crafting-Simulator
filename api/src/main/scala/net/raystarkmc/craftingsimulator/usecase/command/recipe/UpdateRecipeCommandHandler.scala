package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.usecase.command.recipe.UpdateRecipeCommandHandler.*

import java.util.UUID

trait UpdateRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Either[UpdateRecipeCommandHandler.Error, Output]]

object UpdateRecipeCommandHandler extends UpdateRecipeCommandHandlerGivens:
  case class Command(id: UUID, name: String) derives Hash, Show
  case class Output() derives Hash, Show
  enum Error derives Hash, Show:
    case NameError(detail: RecipeName.Failure)
    case NotFound

trait UpdateRecipeCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, RecipeRepository}]
    => UpdateRecipeCommandHandler[F] =
    object instance extends UpdateRecipeCommandHandler[F]:
      private val recipeRepository: RecipeRepository[F] = summon

      def run(
          command: Command
      ): F[Either[UpdateRecipeCommandHandler.Error, Output]] =
        val eitherT: EitherT[F, UpdateRecipeCommandHandler.Error, Output] = for {
          name <- RecipeName
            .ae(command.name)
            .leftMap(_.head)
            .leftMap(UpdateRecipeCommandHandler.Error.NameError.apply)
            .toEitherT[F]
          recipeId = RecipeId(command.id)
          recipe <- EitherT.fromOptionF(
            recipeRepository.resolveById(recipeId),
            UpdateRecipeCommandHandler.Error.NotFound
          )
          updated = recipe.update(name)
          _ <- EitherT.right[UpdateRecipeCommandHandler.Error](
            recipeRepository.save(updated)
          )
        } yield Output()
        eitherT.value
    instance
