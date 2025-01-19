package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository

import cats.*
import cats.data.*
import cats.effect.IO
import cats.syntax.all.given
import doobie.*
import doobie.implicits.given
import doobie.postgres.implicits.given
import net.raystarkmc.craftingsimulator.domain.item.{
  Item,
  ItemId,
  ItemName,
  ItemRepository
}
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.table.ItemTableRecord
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa

import java.util.UUID

trait PGItemRepository extends ItemRepository[IO]:
  override def resolveById(itemId: ItemId): IO[Option[Item]] =
    val query =
      sql"select item.id, item.name from item where id = ${itemId.value.toString}"
        .query[ItemTableRecord]
        .option

    val optionT = for {
      ItemTableRecord(id, name) <- OptionT(query.transact[IO](xa))
      itemId = ItemId(id)
      itemName: ItemName <-
        ItemName.either(name) match {
          case Left(err) =>
            OptionT.liftF(
              new RuntimeException(err.show).raiseError[IO, ItemName]
            )
          case Right(v) =>
            OptionT.some[IO](v)
        }
    } yield {
      Item.restore(
        data = Item.Data(
          id = itemId,
          name = itemName
        )
      )
    }
    optionT.value

  override def save(item: Item): IO[Unit] =
    val insertSql =
      sql"""
        insert
          into item (id, name)
          values (${item.data.id.value}, ${item.data.name.value})
        on conflict(id) do
        update
          set name = excluded.name
      """.update.run

    insertSql.void.transact[IO](xa)

object PGItemRepository extends PGItemRepositoryGivens

trait PGItemRepositoryGivens:
  given ItemRepository[IO] =
    object repository extends PGItemRepository
    repository
