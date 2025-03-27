package net.raystarkmc.craftingsimulator.domain.recipe

import cats.derived.*
import cats.*
import cats.instances.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*
import net.raystarkmc.craftingsimulator.domain.item.{*, given}
import net.raystarkmc.craftingsimulator.domain.item.ItemId.{*, given}
import net.raystarkmc.craftingsimulator.lib.domain.*

sealed trait RecipeContext

type RecipeId = ModelIdUUID[RecipeContext]
object RecipeId extends ModelIdUUIDTypeOps[RecipeContext]
import RecipeId.given

type RecipeName = ModelName[RecipeContext]
object RecipeName extends ModelNameTypeOps[RecipeContext]
import RecipeName.given

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
object Recipe extends RefinedTypeOps[RecipeData, Pure, Recipe]

trait RecipeRepository[F[_]]:
  def resolveById(recipeId: RecipeId): F[Option[Recipe]]
  def save(recipe: Recipe): F[Unit]
  def delete(recipe: Recipe): F[Unit]
