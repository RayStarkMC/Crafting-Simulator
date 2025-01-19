package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler

import cats.effect.IO
import net.raystarkmc.craftingsimulator.usecase.query.GetAllItemsQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.GetAllItemsQueryHandler.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.table.ItemTableRecord
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa
import doobie.*
import doobie.implicits.given
import doobie.postgres.implicits.given

trait PGGetAllItemsQueryHandler extends GetAllItemsQueryHandler[IO]:
  def run(): IO[AllItems] =
    val query = sql"""
      select
        item.id, item.name
      from
        item
      order by
        item.name,
        item.id
    """
      .query[ItemTableRecord]
      .to[Seq]

    for {
      records <- query.transact[IO](xa)
    } yield {
      AllItems(
        list = records.map { record =>
          Item(
            id = record.id,
            name = record.name
          )
        }
      )
    }

object PGGetAllItemsQueryHandler

trait PGGetAllItemsQueryHandlerGivens:
  given GetAllItemsQueryHandler[IO] =
    object handler extends PGGetAllItemsQueryHandler
    handler
