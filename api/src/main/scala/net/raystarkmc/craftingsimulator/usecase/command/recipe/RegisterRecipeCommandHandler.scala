package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.item.{*, given}
import net.raystarkmc.craftingsimulator.domain.item.ItemId.{*, given}
import net.raystarkmc.craftingsimulator.domain.recipe.{Recipe, RecipeName, RecipeRepository}
import net.raystarkmc.craftingsimulator.domain.recipe.Recipe.{*, given}
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeId.{*, given}
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeName.{*, given}
import net.raystarkmc.craftingsimulator.usecase.command.recipe.RegisterRecipeCommandHandler.*

import java.util.UUID

trait RegisterRecipeCommandHandler[F[_]]:
  def run(
      command: Command
  ): F[Either[RegisterRecipeCommandHandler.Error, Output]]

object RegisterRecipeCommandHandler extends RegisterRecipeCommandHandlerGivens:
  case class Command(name: String) derives Hash, Show
  case class Output(id: UUID) derives Hash, Show
  case class Error(detail: RecipeName.Failure) derives Hash, Show

trait RegisterRecipeCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, RecipeRepository}]
    => RegisterRecipeCommandHandler[F] =
    object instance extends RegisterRecipeCommandHandler[F]:
      private val recipeRepository: RecipeRepository[F] = summon

      def run(
          command: Command
      ): F[Either[RegisterRecipeCommandHandler.Error, Output]] =
        val eitherT = for {
          name <- RecipeName
            .ae(command.name)
            .leftMap(_.head)
            .leftMap(RegisterRecipeCommandHandler.Error.apply)
            .toEitherT[F]
          recipe <- EitherT.liftF(
            Recipe.create(
              name = name,
              inputs = ???,
              outputs = ???
            )
          )
          _ <- EitherT.liftF(
            recipeRepository.save(recipe)
          )
        } yield Output(recipe.value.id.value)
        eitherT.value
    instance
