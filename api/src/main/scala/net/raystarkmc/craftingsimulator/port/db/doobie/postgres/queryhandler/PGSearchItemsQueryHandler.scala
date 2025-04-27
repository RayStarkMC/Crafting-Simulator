package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import cats.instances.given
import cats.syntax.all.given
import doobie.*
import doobie.implicits.given
import doobie.postgres.implicits.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGSearchItemsQueryHandler.ItemTableRecord
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa
import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler.*

import java.util.UUID

trait PGSearchItemsQueryHandler[F[_]: Async] extends SearchItemsQueryHandler[F]:
  def run(input: Input): F[Items] =
    val whereClause = input.name.fold(Fragment.empty)(name =>
      fr"WHERE name LIKE ${"%" + name + "%"}"
    )

    val query = sql"""
      select
        item.id, item.name
      from
        item
      $whereClause
      order by
        item.name,
        item.id
    """
      .query[ItemTableRecord]
      .to[Seq]

    for {
      records <- query.transact[F](xa)
    } yield {
      Items(
        list = records.map { record =>
          Item(
            id = record.id,
            name = record.name
          )
        }
      )
    }

object PGSearchItemsQueryHandler extends PGSearchItemsQueryHandlerGivens:
  private case class ItemTableRecord(
    id: UUID,
    name: String
  )

trait PGSearchItemsQueryHandlerGivens:
  given [F[_]: Async] => SearchItemsQueryHandler[F] =
    object handler extends PGSearchItemsQueryHandler
    handler
