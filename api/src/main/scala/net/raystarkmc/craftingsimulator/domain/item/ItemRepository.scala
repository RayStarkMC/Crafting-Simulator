package net.raystarkmc.craftingsimulator.domain.item

import cats.~>

trait ItemRepository[F[_]]:
  def resolveById(itemId: ItemId): F[Option[Item]]
  def save(item: Item): F[Unit]
  def mapK[G[_]](f: F ~> G): ItemRepository[G] = new ItemRepository[G]:
    override def resolveById(itemId: ItemId): G[Option[Item]] = f(ItemRepository.this.resolveById(itemId))
    override def save(item: Item): G[Unit] = f(ItemRepository.this.save(item))