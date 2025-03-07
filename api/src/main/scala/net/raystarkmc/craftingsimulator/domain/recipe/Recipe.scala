package net.raystarkmc.craftingsimulator.domain.recipe

import cats.{Eq, Hash}
import cats.derived.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}
import io.github.iltotore.iron.constraint.all.*
import net.raystarkmc.craftingsimulator.domain.item.{*, given}
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

opaque type RecipeInput = Seq[ItemCount] :| Pure
object RecipeInput extends RefinedTypeOps[Seq[ItemCount], Pure, RecipeInput]

opaque type RecipeOutput = Seq[ItemCount] :| Pure
object RecipeOutput extends RefinedTypeOps[Seq[ItemCount], Pure, RecipeOutput]

case class RecipeData(
    id: RecipeId,
    name: RecipeName,
    inputs: RecipeInput,
    outputs: RecipeOutput
) derives Eq, Hash
opaque type Recipe = RecipeData :| Pure
object Recipe extends RefinedTypeOps[RecipeData, Pure, Recipe]

trait RecipeRepository[F[_]]:
  def resolveById(itemId: ItemId): F[Option[Item]]
  def save(item: Item): F[Unit]
  def delete(item: Item): F[Unit]
