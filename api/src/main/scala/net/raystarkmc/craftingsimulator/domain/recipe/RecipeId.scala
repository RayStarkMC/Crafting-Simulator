package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.syntax.all.given
import cats.effect.std.UUIDGen

import java.util.UUID

opaque type RecipeId = UUID

object RecipeId extends RecipeIdGivens:
  extension (self: RecipeId) def unwrap: UUID = self
  def apply(self: UUID): RecipeId = self

  def generate[F[_]: UUIDGen]: F[RecipeId] =
    UUIDGen.randomUUID

trait RecipeIdGivens:
  given Hash[RecipeId] = Hash.by(_.unwrap)
  given Show[RecipeId] = Show.show(_.unwrap.show)
