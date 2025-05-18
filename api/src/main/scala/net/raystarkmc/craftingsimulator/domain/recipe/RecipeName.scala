package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.data.*
import cats.implicits.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName

opaque type RecipeName = ModelName

object RecipeName extends RecipeNameGivens:
  extension (self: RecipeName) def value: ModelName = self

  def apply(modelName: ModelName): RecipeName = modelName

private inline def wrapF[F[_]](f: F[ModelName]): F[RecipeName] = f

trait RecipeNameGivens:
  given Eq[RecipeName] = wrapF(Eq[ModelName])
  given Hash[RecipeName] = wrapF(Hash[ModelName])
  given Order[RecipeName] = wrapF(Order[ModelName])
  given Show[RecipeName] = wrapF(Show[ModelName])
