package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler

import cats.*
import cats.data.*
import cats.effect.*
import doobie.*
import doobie.implicits.given
import doobie.postgres.implicits.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGGetItemQueryHandler.ItemTableRecord
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler.*

import java.util.UUID

trait PGGetItemQueryHandler[F[_]: Async] extends GetItemQueryHandler[F]:
  def run(input: Input): F[Option[Item]] =
    val query = sql"""
      select
        item.id, item.name
      from
        item
      where
        item.id = ${input.id}
      order by
        item.name,
        item.id
    """
      .query[ItemTableRecord]
      .option

    OptionT(query.transact[F](xa)).map { record =>
      Item(
        id = record.id,
        name = record.name
      )
    }.value

object PGGetItemQueryHandler extends PGGetItemQueryHandlerGivens:
  private case class ItemTableRecord(
    id: UUID,
    name: String
  )

trait PGGetItemQueryHandlerGivens:
  given [F[_]: Async] => GetItemQueryHandler[F] =
    object handler extends PGGetItemQueryHandler
    handler
