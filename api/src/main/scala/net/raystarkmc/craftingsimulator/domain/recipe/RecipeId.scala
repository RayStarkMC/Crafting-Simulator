package net.raystarkmc.craftingsimulator.domain.recipe

import cats.effect.std.UUIDGen
import cats.{Eq, Hash, Order, Show}

import java.util.UUID

opaque type RecipeId = UUID
object RecipeId extends RecipeIdGivens:
  extension (self: RecipeId) def value: UUID = self

  def apply(value: UUID): RecipeId = value

  def generate[F[_] : UUIDGen]: F[RecipeId] =
    wrapRecipeIdF(UUIDGen.randomUUID)

private inline def wrapRecipeIdF[F[_]](f: F[UUID]): F[RecipeId] = f

private trait RecipeIdGivens:
  given Eq[RecipeId] = wrapRecipeIdF(Eq[UUID])
  given Hash[RecipeId] = wrapRecipeIdF(Hash[UUID])
  given Order[RecipeId] = wrapRecipeIdF(Order[UUID])
  given Show[RecipeId] = wrapRecipeIdF(Show[UUID])