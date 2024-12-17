package net.raystarkmc.craftingsimulator.domain.item

import cats.Functor
import cats.syntax.all.given 
import cats.effect.std.UUIDGen

import java.util.UUID

case class ItemId(value: UUID)

object ItemId:
  def generate[F[_] : UUIDGen : Functor]: F[ItemId] =
    UUIDGen.randomUUID.map(ItemId.apply)