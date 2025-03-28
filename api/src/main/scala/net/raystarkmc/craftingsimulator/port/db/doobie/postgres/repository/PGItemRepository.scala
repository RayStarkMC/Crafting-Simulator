package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import doobie.*
import doobie.implicits.given
import doobie.postgres.*
import doobie.postgres.implicits.given
import io.github.iltotore.iron.*
import io.github.iltotore.iron.doobie.given
import net.raystarkmc.craftingsimulator.domain.item.{*, given}
import net.raystarkmc.craftingsimulator.domain.item.ItemId.{*, given}
import net.raystarkmc.craftingsimulator.domain.item.ItemName.{*, given}
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.table.ItemTableRecord
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa

import java.util.UUID

trait PGItemRepository[F[_]: Async] extends ItemRepository[F]:
  override def resolveById(itemId: ItemId): F[Option[Item]] =
    val query =
      sql"select item.id, item.name from item where id = $itemId"
        .query[ItemTableRecord]
        .option

    val optionT = for {
      ItemTableRecord(id, name) <- OptionT(query.transact[F](xa))
      itemId = ItemId(id)
      itemName: ItemName <-
        ItemName.ae(name) match {
          case Left(err) =>
            OptionT.liftF(
              new RuntimeException(err.show).raiseError[F, ItemName]
            )
          case Right(v) =>
            OptionT.some[F](v)
        }
    } yield {
      Item(
        ItemData(
          id = itemId,
          name = itemName
        )
      )
    }
    optionT.value

  override def save(item: Item): F[Unit] =
    val insertSql =
      sql"""
        insert
          into item (id, name, created_at, updated_at)
          values (
            ${item.value.id},
            ${item.value.name},
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
        item.id = ${item.value.id}
    """.update.run

    deleteSql.void.transact[F](xa)

object PGItemRepository:
  given [F[_] : Async] => ItemRepository[F] =
    object repository extends PGItemRepository
    repository