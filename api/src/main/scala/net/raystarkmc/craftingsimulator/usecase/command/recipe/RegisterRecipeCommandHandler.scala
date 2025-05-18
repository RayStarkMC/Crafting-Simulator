package net.raystarkmc.craftingsimulator.usecase.command.recipe

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.*

import java.util.UUID
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.domain.recipe.{ItemCount, *}
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
        (name, inputs, outputs) <-
          (
            ModelName
              .inParallel[EitherTWithNec[F, ModelName.Failure]](command.name)
              .leftMap: e =>
                Failure.ValidationFailed(e.show)
              .map:
                RecipeName.apply
            ,
            EitherT.fromEither[F]:
              command.inputs
                .traverse: (uuid, count) =>
                  ItemCount
                    .ae[ValidatedNec[ItemCount.Failure, _]](count).toEither
                    .map: c =>
                      ItemWithCount(
                        item = ItemId(uuid),
                        count = c,
                      )
                .map:
                  RecipeInput.apply
                .leftMap: e =>
                  Failure.ValidationFailed(e.show)
            ,
            EitherT.fromEither[F]:
              command.outputs
                .traverse: (uuid, count) =>
                  ItemCount
                    .ae[ValidatedNec[ItemCount.Failure, _]](count).toEither
                    .map: c =>
                      ItemWithCount(
                        item = ItemId(uuid),
                        count = c,
                      )
                .map:
                  RecipeOutput.apply
                .leftMap: e =>
                  Failure.ValidationFailed(e.show),
          ).tupled
        recipe <- EitherT.liftF:
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
