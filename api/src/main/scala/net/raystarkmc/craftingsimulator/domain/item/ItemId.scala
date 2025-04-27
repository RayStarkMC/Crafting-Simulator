package net.raystarkmc.craftingsimulator.domain.item

import cats.effect.std.UUIDGen
import cats.{Eq, Hash, Order, Show}

import java.util.UUID

opaque type ItemId = UUID
object ItemId extends ItemIdGivens:
  extension (self: ItemId) def value: UUID = self

  def apply(value: UUID): ItemId = value

  def generate[F[_]: UUIDGen]: F[ItemId] =
    wrapItemIdF(UUIDGen.randomUUID)

private inline def wrapItemIdF[F[_]](f: F[UUID]): F[ItemId] = f

private trait ItemIdGivens:
  given Eq[ItemId] = wrapItemIdF(Eq[UUID])
  given Hash[ItemId] = wrapItemIdF(Hash[UUID])
  given Order[ItemId] = wrapItemIdF(Order[UUID])
  given Show[ItemId] = wrapItemIdF(Show[UUID])
