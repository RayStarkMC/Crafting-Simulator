package net.raystarkmc.craftingsimulator.domain.item

import cats.derived.*
import cats.effect.std.UUIDGen
import cats.syntax.all.given
import cats.{Functor, Hash, Show}
import net.raystarkmc.craftingsimulator.lib.domain.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}

private sealed trait ItemContext

type ItemId = ItemId.T
object ItemId extends ModelIdUUID[ItemContext]

type ItemName = ItemName.T
object ItemName extends ModelName[ItemContext]

case class ItemData(
    id: ItemId,
    name: ItemName
) derives Hash,
      Show
type Item = Item.T

object Item extends RefinedType[ItemData, Pure]:
  extension (self: Item)
    def update(newName: ItemName): Item =
      Item(
        self.copy(name = newName)
      )

  def create[F[_]: {Functor, UUIDGen}](name: ItemName): F[Item] =
    ItemId.generate
      .map(ItemData(_, name))
      .map(Item(_))

trait ItemRepository[F[_]]:
  def resolveById(itemId: ItemId): F[Option[Item]]
  def save(item: Item): F[Unit]
  def delete(item: Item): F[Unit]

