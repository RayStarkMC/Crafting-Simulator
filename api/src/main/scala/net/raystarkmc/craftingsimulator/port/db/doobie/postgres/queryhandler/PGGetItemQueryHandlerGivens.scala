package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler

import cats.*
import cats.data.*
import cats.effect.*
import doobie.*
import doobie.implicits.given
import doobie.postgres.implicits.given
import net.raystarkmc.craftingsimulator.usecase.query.item.GetItemQueryHandler.*
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.query.item.GetItemQueryHandler

import java.util.UUID

trait PGGetItemQueryHandlerGivens:
  given [F[_]] => (T: Transaction[ConnectionIO, F]) => GetItemQueryHandler[F]:
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

      T.withTransaction {
        val optionT = for {
          record <- OptionT(query.option)
          item = Item(
            id = record.id,
            name = record.name
          )
        } yield item
        optionT.value
      }
