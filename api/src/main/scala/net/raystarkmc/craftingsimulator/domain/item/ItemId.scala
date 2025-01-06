package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.Functor
import cats.syntax.all.given 
import cats.effect.std.UUIDGen

import java.util.UUID

opaque type ItemId = UUID

object ItemId:
  extension (self: ItemId)
    def value: UUID = self
  def apply(self: UUID): ItemId = self

  def generate[F[_] : UUIDGen : Functor]: F[ItemId] =
    UUIDGen.randomUUID

  given Hash[ItemId] = Hash.fromUniversalHashCode
  given Show[ItemId] = Show.fromToString