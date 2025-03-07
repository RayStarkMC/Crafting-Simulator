package net.raystarkmc.craftingsimulator.domain.item

import cats.derived.*
import cats.effect.std.UUIDGen
import cats.syntax.all.given
import cats.{Functor, Hash, Show}
import net.raystarkmc.craftingsimulator.lib.domain.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}

private sealed trait ItemContext

type ItemId = ModelIdUUID[ItemContext]
object ItemId extends ModelIdUUIDTypeOps[ItemContext]
import ItemId.given

type ItemName = ModelName[ItemContext]
object ItemName extends ModelNameTypeOps[ItemContext]
import ItemName.given

case class ItemData(
    id: ItemId,
    name: ItemName
) derives Hash,
      Show
opaque type Item = ItemData :| Pure

object Item extends RefinedTypeOps[ItemData, Pure, Item]:

  extension (self: Item)
    def update(newName: ItemName): Item =
      self.copy(name = newName)

  def create[F[_]: {Functor, UUIDGen}](name: ItemName): F[Item] =
    ItemId.generate.map(ItemData(_, name))

trait ItemRepository[F[_]]:
  def resolveById(itemId: ItemId): F[Option[Item]]
  def save(item: Item): F[Unit]
  def delete(item: Item): F[Unit]

