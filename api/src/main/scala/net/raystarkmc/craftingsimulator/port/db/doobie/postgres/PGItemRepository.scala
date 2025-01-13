package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import cats.Monad
import net.raystarkmc.craftingsimulator.domain.item.{Item, ItemId, ItemRepository}
import cats.syntax.all.given
import cats.instances.all.given
import cats.data.*
import cats.effect.Async
import doobie.*
import doobie.implicits.given

trait PGItemRepository[F[_]: Async] extends ItemRepository[F]:
  //FIXME: 実際のDBにアクセスしてアイテムを取得する
  override def resolveById(itemId: ItemId): F[Option[Item]] =
    val program = Option.empty[Item].pure[ConnectionIO]
    program.transact[F](xa)

  //FIXME: 実際のDBにアクセスしてアイテムを登録する
  override def save(item: Item): F[Unit] =
    val program = ().pure[ConnectionIO]
    program.transact[F](xa)

object PGItemRepository extends PGItemRepositoryGivens

trait PGItemRepositoryGivens:
  given [F[_] : Async]: PGItemRepository[F] =
    object repository extends PGItemRepository[F]
    repository