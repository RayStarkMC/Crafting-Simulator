package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler

import cats.*
import cats.data.*
import cats.effect.*
import doobie.*
import doobie.implicits.given
import doobie.postgres.implicits.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler.*

import java.util.UUID

trait PGGetItemQueryHandlerGivens:
  given [F[_]: Async] => GetItemQueryHandler[F]:
    def run(input: Input): F[Option[Item]] =
      case class ItemTableRecord(
        id: UUID,
        name: String
      )
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

