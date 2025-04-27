package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.instances.all.given

opaque type RecipeOutput = Seq[ItemWithCount]
object RecipeOutput extends RecipeOutputGivens:
  inline def apply(data: Seq[ItemWithCount]): RecipeOutput = data
  extension (self: RecipeOutput) inline def value: Seq[ItemWithCount] = self

private inline def wrapRecipeOutputF[F[_]](
    f: F[Seq[ItemWithCount]]
): F[RecipeOutput] = f

trait RecipeOutputGivens:
  given Eq[RecipeOutput] = wrapRecipeOutputF(Eq[Seq[ItemWithCount]])
  given Hash[RecipeOutput] = wrapRecipeOutputF(Hash[Seq[ItemWithCount]])
  given Show[RecipeOutput] = wrapRecipeOutputF(Show[Seq[ItemWithCount]])
