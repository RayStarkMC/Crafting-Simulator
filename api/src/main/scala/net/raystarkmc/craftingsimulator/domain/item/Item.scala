package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.syntax.all.given
import cats.derived.*
import cats.effect.std.UUIDGen

case class Item private (id: ItemId, name: ItemName) derives Eq, Hash, Show:
  def update(newName: ItemName): Item = copy(name = newName)

object Item:
  def create[F[_]: {Functor, UUIDGen}](name: ItemName): F[Item] =
    ItemId.generate.map(Item(_, name))
    
  def restore(id: ItemId, name: ItemName): Item = Item(id, name)
