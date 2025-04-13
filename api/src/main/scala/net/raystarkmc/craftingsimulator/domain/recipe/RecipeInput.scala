package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.implicits.*
import net.raystarkmc.craftingsimulator.domain.recipe.RecipeInput.*

opaque type RecipeInput <: Data = Data
object RecipeInput extends RecipeInputGivens:
  type Data = Seq[ItemWithCount]

  inline def apply(data: Data): RecipeInput = data

  extension(self: RecipeInput)
    inline def value: Data = self

  extension [F[_]](self: F[Data])
    inline def mask: F[RecipeInput] = self

trait RecipeInputGivens:
  given Hash[RecipeInput] = Hash[Data].mask
  given Show[RecipeInput] = Show[Data].mask
