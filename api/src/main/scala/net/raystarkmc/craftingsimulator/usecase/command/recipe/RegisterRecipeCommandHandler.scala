package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.usecase.command.recipe.RegisterRecipeCommandHandler.*

import java.util.UUID

trait RegisterRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Either[RegisterRecipeCommandHandler.Error, Output]]

object RegisterRecipeCommandHandler:
  case class Command(
      name: String,
      inputs: Seq[(UUID, Long)],
      outputs: Seq[(UUID, Long)]
  ) derives Hash,
        Show
  case class Output(id: UUID) derives Hash, Show
  case class Error(detail: String) derives Hash, Show

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
            .leftMap(_.toString)
            .leftMap(RegisterRecipeCommandHandler.Error(_))
            .toEitherT[F]
          inputs: RecipeInput <- EitherT.fromEither[F] {
            command.inputs
              .traverse { (uuid, count) =>
                ItemCount.ae(count).map { c =>
                  ItemWithCount(
                    item = ItemId(uuid),
                    count = c
                  )
                }
              }
              .map { icSeq =>
                RecipeInput.apply(icSeq)
              }
              .leftMap(_.toString)
              .leftMap(RegisterRecipeCommandHandler.Error(_))
          }
          outputs: RecipeOutput <- EitherT.fromEither[F] {
            command.outputs
              .traverse { (uuid, count) =>
                ItemCount.ae(count).map { c =>
                  ItemWithCount(
                    item = ItemId(uuid),
                    count = c
                  )
                }
              }
              .map { icSeq =>
                RecipeOutput.apply(icSeq)
              }
              .leftMap(_.toString)
              .leftMap(RegisterRecipeCommandHandler.Error(_))
          }

          recipe <- EitherT.liftF(
            Recipe.create(
              name = name,
              inputs = inputs,
              outputs = outputs
            )
          )
          _ <- EitherT.liftF(
            recipeRepository.save(recipe)
          )
        } yield Output(recipe.id.value)
        eitherT.value
    instance
