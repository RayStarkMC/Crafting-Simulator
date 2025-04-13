package net.raystarkmc.craftingsimulator.domain.item

import cats.derived.*
import cats.effect.std.UUIDGen
import cats.syntax.all.given
import cats.{Functor, Hash, Show}
import net.raystarkmc.craftingsimulator.lib.domain.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}

case class Item private (id: ItemId, name: ItemName) derives Hash, Show:
  def update(newName: ItemName): Item = copy(name = newName)

object Item:
  def create[F[_]: {Functor, UUIDGen}](name: ItemName): F[Item] =
    ItemId.generate.map(Item(_, name))
    
  def restore(id: ItemId, name: ItemName): Item = Item(id, name)
