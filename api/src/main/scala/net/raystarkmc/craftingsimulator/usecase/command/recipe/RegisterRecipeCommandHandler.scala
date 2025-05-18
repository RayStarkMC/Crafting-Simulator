package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.*
import java.util.UUID
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.domain.recipe.ItemCount
import net.raystarkmc.craftingsimulator.lib.cats.*
import net.raystarkmc.craftingsimulator.lib.domain.*
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.command.recipe.RegisterRecipeCommandHandler.*

trait RegisterRecipeCommandHandler[F[_]]:
  def run(command: Command): F[Either[Failure, Output]]

object RegisterRecipeCommandHandler:
  case class Command(
    name: String,
    inputs: Seq[(UUID, Long)],
    outputs: Seq[(UUID, Long)],
  ) derives Eq,
      Hash,
      Show
  case class Output(id: UUID) derives Eq, Hash, Show
  enum Failure derives Eq, Hash, Show:
    case ValidationFailed(detail: String)

  given [
    F[_]: {UUIDGen, Monad},
    G[_]: {RecipeRepository as recipeRepository, Monad},
  ] => (T: Transaction[G, F]) => RegisterRecipeCommandHandler[F]:
    def run(command: Command): F[Either[Failure, Output]] =
      val eitherT = for
        (name, inputs, outputs) <- EitherT.fromEither[F]:
          (
            ModelName
              .inParallel[EitherNec[ModelName.Failure, _]](command.name)
              .leftMap: e =>
                Failure.ValidationFailed(e.show)
              .map:
                RecipeName.apply
            ,
            command.inputs
              .traverse: (uuid, count) =>
                ItemCount
                  .ae[ValidatedNec[ItemCount.Failure, _]](count)
                  .toEither
                  .leftMap: e =>
                    Failure.ValidationFailed(e.show)
                  .map: c =>
                    ItemWithCount(
                      item = ItemId(uuid),
                      count = c,
                    )
              .map:
                RecipeInput.apply
            ,
            command.outputs
              .traverse: (uuid, count) =>
                ItemCount
                  .ae[ValidatedNec[ItemCount.Failure, _]](count)
                  .toEither
                  .leftMap: e =>
                    Failure.ValidationFailed(e.show)
                  .map: c =>
                    ItemWithCount(
                      item = ItemId(uuid),
                      count = c,
                    )
              .map:
                RecipeOutput.apply,
          ).tupled
        recipe <- EitherT.right[Failure]:
          Recipe.create[F](
            name = name,
            inputs = inputs,
            outputs = outputs,
          )
        _ <- T.withTransaction:
          EitherT.right[Failure]:
            recipeRepository.save(recipe)
        output = Output(recipe.id.value)
      yield output
      eitherT.value
