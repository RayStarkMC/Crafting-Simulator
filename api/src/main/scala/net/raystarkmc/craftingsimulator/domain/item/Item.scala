package net.raystarkmc.craftingsimulator.domain.item

import cats.derived.*
import cats.effect.std.UUIDGen
import cats.syntax.all.given
import cats.{Functor, Hash, Show}
import net.raystarkmc.craftingsimulator.domain.item.Item.Data
import net.raystarkmc.craftingsimulator.lib.domain.{ModelIdUUID, ModelIdUUIDTypeOps, ModelName, ModelNameTypeOps}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}

private sealed trait ItemContext

type ItemId = ModelIdUUID[ItemContext]
object ItemId extends ModelIdUUIDTypeOps[ItemContext]
export ItemId.given

type ItemName = ModelName[ItemContext]
object ItemName extends ModelNameTypeOps[ItemContext]
export ItemName.given

opaque type Item = Data

object Item extends ItemGivens:
  case class Data(
      id: ItemId,
      name: ItemName
  ) derives Hash,
        Show

  extension (self: Item)
    def data: Data = self
    def update(newName: ItemName): Item =
      self.copy(name = newName)

  def restore(data: Data): Item = data
  def create[F[_]: {Functor, UUIDGen}](name: ItemName): F[Item] =
    ItemId.generate.map(Data(_, name))

trait ItemGivens:
  given Hash[Item] = Hash.by(_.data)

  given Show[Item] = Show.show(_.data.show)