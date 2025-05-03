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
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction

import java.util.UUID

trait RegisterRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Either[Failure, Output]]

object RegisterRecipeCommandHandler:
  case class Command(
      name: String,
      inputs: Seq[(UUID, Long)],
      outputs: Seq[(UUID, Long)]
  ) derives Eq,
        Hash,
        Show
  case class Output(id: UUID) derives Eq, Hash, Show
  enum Failure derives Eq, Hash, Show:
    case ValidationFailed(detail: String)

  given [
      F[_]: {UUIDGen, Monad},
      G[_]: {RecipeRepository as recipeRepository, Monad}
  ] => (T: Transaction[G, F]) => RegisterRecipeCommandHandler[F]:
    def run(command: Command): F[Either[Failure, Output]] =
      val eitherT = for {
        name <- RecipeName
          .ae(command.name)
          .leftMap(_.toString)
          .leftMap(Failure.ValidationFailed(_))
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
            .leftMap(Failure.ValidationFailed(_))
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
            .leftMap(Failure.ValidationFailed(_))
        }

        recipe <- EitherT.liftF(
          Recipe.create[F](
            name = name,
            inputs = inputs,
            outputs = outputs
          )
        )
        _ <- T.withTransaction {
          EitherT.right[Failure](
            recipeRepository.save(recipe)
          )
        }
      } yield Output(recipe.id.value)
      eitherT.value
