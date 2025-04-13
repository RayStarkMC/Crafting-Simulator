package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.effect.std.UUIDGen
import cats.implicits.*
import cats.derived.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}

case class Recipe private (
    id: RecipeId,
    name: RecipeName,
    input: RecipeInput,
    output: RecipeOutput
) derives Hash, Show:
  def update(newName: RecipeName): Recipe =
    copy(name = newName)

object Recipe:
  def create[F[_]: {Functor, UUIDGen}](
      name: RecipeName,
      inputs: RecipeInput,
      outputs: RecipeOutput
  ): F[Recipe] =
    RecipeId.generate.map(
      Recipe(_, name, inputs, outputs)
    )

  def restore(
      id: RecipeId,
      name: RecipeName,
      inputs: RecipeInput,
      outputs: RecipeOutput
  ): Recipe =
    Recipe(id, name, inputs, outputs)
