package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.implicits.*
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeOutput.*

opaque type RecipeOutput <: Data = Data
object RecipeOutput extends RecipeOutputGivens:
  type Data = Seq[ItemWithCount]

  inline def apply(data: Data): RecipeOutput = data

  extension(self: RecipeOutput)
    inline def value: Data = self

  extension [F[_]](self: F[Data])
    inline def mask: F[RecipeOutput] = self

trait RecipeOutputGivens:
  given Hash[RecipeOutput] = Hash[Data].mask
  given Show[RecipeOutput] = Show[Data].mask