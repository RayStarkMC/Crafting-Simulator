package net.raystarkmc.craftingsimulator.domain.item

trait ItemRepository[F[_]]:
  def resolveById(itemId: ItemId): F[Option[Item]]
  def save(item: Item): F[Unit]
