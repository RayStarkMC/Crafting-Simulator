package net.raystarkmc.craftingsimulator.domain.item

import cats.effect.std.UUIDGen
import cats.syntax.all.given
import cats.{Functor, Hash, Show}
import cats.derived.*
import net.raystarkmc.craftingsimulator.domain.item.Item.Data
import net.raystarkmc.craftingsimulator.domain.item.ItemName

opaque type Item = Data

object Item:
  case class Data(
    id: ItemId,
    name: ItemName,
  ) derives Hash, Show

  extension (self: Item)
    def data: Data = self
    def update(newName: ItemName): Item =
      self.copy(name = newName)

  def restore(data: Data): Item = data
  def create[F[_] : Functor : UUIDGen](name: ItemName): F[Item] =
    ItemId.generate.map(Data(_, name))

object ItemGivens:
  given Hash[Item] = Hash.by(_.data)
  given Show[Item] = Show.show(_.data.show)