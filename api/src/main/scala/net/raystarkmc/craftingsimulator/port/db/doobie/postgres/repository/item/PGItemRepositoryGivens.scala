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
import net.raystarkmc.craftingsimulator.lib.domain.ModelName

import java.util.UUID

trait PGItemRepositoryGivens:
  private case class ItemTableRecord(
      id: UUID,
      name: String
  )

  given ItemRepository[ConnectionIO]:
    def resolveById(itemId: ItemId): ConnectionIO[Option[Item]] =
      val query =
        sql"select item.id, item.name from item where id = ${itemId.value}"
          .query[ItemTableRecord]

      val transactionT = for {
        ItemTableRecord(id, name) <- OptionT {
          query.option
        }
        itemId = ItemId(id)
        itemName: ItemName <- OptionT.liftF {
          ApplicativeThrow[ConnectionIO].fromEither {
            ModelName
              .inParallel[EitherNec[ModelName.Failure, _]](name)
              .leftMap(_.show)
              .map(ItemName.apply)
              .leftMap(IllegalStateException(_))
          }
        }
        restoredItem = Item.restore(
          id = itemId,
          name = itemName
        )
      } yield restoredItem

      transactionT.value

    def save(item: Item): ConnectionIO[Unit] =
      val insertSql =
        sql"""
            insert
              into item (id, name, created_at, updated_at)
              values (
                ${item.id.value},
                ${item.name.value.value},
                current_timestamp,
                current_timestamp
              )
            on conflict(id) do
            update
            set name = excluded.name,
                updated_at = current_timestamp
          """.update.run
      insertSql.void

    def delete(item: Item): ConnectionIO[Unit] =
      val deleteSql =
        sql"""
          delete
          from
            item
          where
            item.id = ${item.id.value}
        """.update.run
      deleteSql.void