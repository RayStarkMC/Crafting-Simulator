package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.effect.std.UUIDGen
import cats.syntax.all.given

import java.util.UUID

opaque type ItemId = UUID

object ItemId extends ItemIdGivens:
  extension (self: ItemId) def unwrap: UUID = self
  def apply(self: UUID): ItemId = self

  def generate[F[_]: UUIDGen]: F[ItemId] =
    UUIDGen.randomUUID

trait ItemIdGivens:
  given Hash[ItemId] = Hash.by(_.unwrap)
  given Show[ItemId] = Show.show(_.unwrap.show)
