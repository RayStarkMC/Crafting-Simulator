package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.implicits.*
import cats.derived.*
import cats.effect.std.UUIDGen
import io.github.iltotore.iron.*
import cats.{*, given}
import io.github.iltotore.iron.constraint.all.*
import net.raystarkmc.craftingsimulator.domain.item.ItemId.given
import net.raystarkmc.craftingsimulator.domain.item.{*, given}
import net.raystarkmc.craftingsimulator.domain.item.ItemId.{*, given}
import net.raystarkmc.craftingsimulator.lib.domain.*

sealed trait RecipeContext

type RecipeId = ModelIdUUID[RecipeContext]
object RecipeId extends ModelIdUUIDTypeOps[RecipeContext]
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeId.given

type RecipeName = ModelName[RecipeContext]
object RecipeName extends ModelNameTypeOps[RecipeContext]
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeName.given

opaque type ItemCount = Long :| Greater[0]
object ItemCount extends RefinedTypeOps[Long, Greater[0], ItemCount]

case class ItemWithCount(item: ItemId, count: ItemCount) derives Hash, Show

opaque type RecipeInput = Seq[ItemWithCount] :| Pure
object RecipeInput extends RefinedTypeOps[Seq[ItemWithCount], Pure, RecipeInput]

opaque type RecipeOutput = Seq[ItemWithCount] :| Pure
object RecipeOutput
    extends RefinedTypeOps[Seq[ItemWithCount], Pure, RecipeOutput]

case class RecipeData(
    id: RecipeId,
    name: RecipeName,
    inputs: RecipeInput,
    outputs: RecipeOutput
) derives Hash,
      Show

opaque type Recipe = RecipeData :| Pure
object Recipe extends RefinedTypeOps[RecipeData, Pure, Recipe]:
  extension (self: Recipe)
    def update(newName: RecipeName): Recipe =
      self.copy(name = newName)

  def create[F[_]: {Functor, UUIDGen}](
      name: RecipeName,
      inputs: RecipeInput,
      outputs: RecipeOutput
  ): F[Recipe] =
    RecipeId.generate.map(
      RecipeData(_, name, inputs, outputs)
    )

trait RecipeRepository[F[_]]:
  def resolveById(recipeId: RecipeId): F[Option[Recipe]]
  def save(recipe: Recipe): F[Unit]
  def delete(recipe: Recipe): F[Unit]
