package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler

import cats.*
import cats.data.*
import cats.syntax.all.given
import cats.instances.given
import cats.effect.*
import cats.effect.syntax.all.given
import cats.effect.instances.all.given
import net.raystarkmc.craftingsimulator.usecase.query.GetAllItemsQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.GetAllItemsQueryHandler.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.table.ItemTableRecord
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa
import doobie.*
import doobie.implicits.given
import doobie.postgres.implicits.given

trait PGGetAllItemsQueryHandler[F[_] : Async] extends GetAllItemsQueryHandler[F]:
  def run(): F[AllItems] =
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
      records <- query.transact[F](xa)
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
  given[F[_] : Async]: GetAllItemsQueryHandler[F] =
    object handler extends PGGetAllItemsQueryHandler
    handler
