package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import cats.{Applicative, Hash, Monad, Show}
import net.raystarkmc.craftingsimulator.domain.recipe.{
  RecipeId,
  RecipeRepository
}
import net.raystarkmc.craftingsimulator.usecase.command.DeleteItemCommandHandler.{
  Command,
  Output
}

import java.util.UUID

trait DeleteRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Output]

object DeleteRecipeCommandHandler extends DeleteRecipeCommandHandlerGivens:
  case class Command(id: UUID) derives Hash, Show
  case class Output() derives Hash, Show

trait DeleteRecipeCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, RecipeRepository}]
    => DeleteRecipeCommandHandler[F] =
    object instance extends DeleteRecipeCommandHandler[F]:
      private val recipeRepository = summon[RecipeRepository[F]]

      def run(
          command: Command
      ): F[Output] =
        val recipeId = RecipeId(command.id)
        for {
          recipeOpt <- recipeRepository.resolveById(recipeId)
          _ <- recipeOpt.fold {
            Applicative[F].unit
          } { item =>
            recipeRepository.delete(item)
          }
        } yield Output()
    instance
