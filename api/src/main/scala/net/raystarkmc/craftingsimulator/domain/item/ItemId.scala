package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.Functor
import cats.syntax.all.given 
import cats.effect.std.UUIDGen

import java.util.UUID

opaque type ItemId = UUID

object ItemId extends ItemIdGivens:
  extension (self: ItemId)
    def value: UUID = self
  def apply(self: UUID): ItemId = self

  def generate[F[_] : UUIDGen : Functor]: F[ItemId] =
    UUIDGen.randomUUID

trait ItemIdGivens:
  given Hash[ItemId] = Hash.by(_.value)
  given Show[ItemId] = Show.show(_.value.show)
