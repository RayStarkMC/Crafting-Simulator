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
import net.raystarkmc.craftingsimulator.usecase.query.item.SearchItemsQueryHandler.*
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.query.item.SearchItemsQueryHandler

import java.util.UUID

trait PGSearchItemsQueryHandlerGivens:
  given [F[_]] => (T: Transaction[ConnectionIO, F]) => SearchItemsQueryHandler[F]:
    def run(input: Input): F[Items] =
      case class ItemTableRecord(
          id: UUID,
          name: String
      )
      val whereClause = input.name.fold(Fragment.empty)(name => fr"WHERE name LIKE ${"%" + name + "%"}")

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

      T.withTransaction {
        for {
          records <- query.to[Seq]
          items = Items(
            list = records.map { record =>
              Item(
                id = record.id,
                name = record.name
              )
            }
          )
        } yield items
      }
