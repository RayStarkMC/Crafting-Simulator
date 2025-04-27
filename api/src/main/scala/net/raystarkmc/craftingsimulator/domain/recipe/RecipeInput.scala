package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.instances.all.given

opaque type RecipeInput = Seq[ItemWithCount]
object RecipeInput extends RecipeInputGivens:
  inline def apply(data: Seq[ItemWithCount]): RecipeInput = data

  extension (self: RecipeInput) inline def value: Seq[ItemWithCount] = self

private inline def wrapRecipeInputF[F[_]](
    f: F[Seq[ItemWithCount]]
): F[RecipeInput] = f

trait RecipeInputGivens:
  given Eq[RecipeInput] = wrapRecipeInputF(Eq[Seq[ItemWithCount]])
  given Hash[RecipeInput] = wrapRecipeInputF(Hash[Seq[ItemWithCount]])
  given Show[RecipeInput] = wrapRecipeInputF(Show[Seq[ItemWithCount]])
