package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.data.*
import cats.implicits.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName

opaque type RecipeName = ModelName

object RecipeName extends RecipeNameGivens:
  extension (self: RecipeName) def value: String = unwrap(self)

  def apply(modelName: ModelName): RecipeName = modelName

  type Failure = ModelName.Failure
  val Failure: ModelName.Failure.type = ModelName.Failure

  def ae[F[_]](value: String)(using ApplicativeError[F, NonEmptyChain[Failure]]): F[RecipeName] =
    ModelName.ae(value)

private inline def unwrap(modelName: ModelName): String = modelName.value
private inline def wrapF[F[_]](f: F[ModelName]): F[RecipeName] = f

trait RecipeNameGivens:
  given Eq[RecipeName] = wrapF(Eq[ModelName])
  given Hash[RecipeName] = wrapF(Hash[ModelName])
  given Order[RecipeName] = wrapF(Order[ModelName])
  given Show[RecipeName] = wrapF(Show[ModelName])
