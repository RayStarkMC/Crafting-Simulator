package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.item

import cats.*
import cats.data.*
import cats.effect.*
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.*
import doobie.postgres.implicits.given
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa

import java.util.UUID

object PGItemRepository:
  private case class ItemTableRecord(
    id: UUID,
    name: String
  )

  given [F[_]: Async] => ItemRepository[F] =
    object repository extends ItemRepository[F]:
      override def resolveById(itemId: ItemId): F[Option[Item]] =
        val query =
          sql"select item.id, item.name from item where id = ${itemId.value}"
            .query[ItemTableRecord]

        val transactionT = for {
          ItemTableRecord(id, name) <- OptionT {
            query.option
          }
          itemId = ItemId(id)
          itemName: ItemName <- OptionT.liftF {
            ItemName
              .ae[[A] =>> ValidatedNec[ItemName.Failure, A]](name)
              .fold[ConnectionIO[ItemName]](
                err => new RuntimeException(err.show).raiseError,
                _.pure
              )
          }
        } yield {
          Item.restore(
            id = itemId,
            name = itemName
          )
        }

        transactionT.value.transact[F](xa)

      override def save(item: Item): F[Unit] =
        val insertSql =
          sql"""
            insert
              into item (id, name, created_at, updated_at)
              values (
                ${item.id.value},
                ${item.name.value},
                current_timestamp,
                current_timestamp
              )
            on conflict(id) do
            update
            set name = excluded.name,
                updated_at = current_timestamp
          """.update.run

        insertSql.void.transact[F](xa)

      override def delete(item: Item): F[Unit] =
        val deleteSql =
          sql"""
          delete
          from
            item
          where
            item.id = ${item.id.value}
        """.update.run

        deleteSql.void.transact[F](xa)
    repository
