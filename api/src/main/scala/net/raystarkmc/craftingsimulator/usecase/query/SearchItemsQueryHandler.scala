package net.raystarkmc.craftingsimulator.usecase.query

import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler.*

import java.util.UUID

trait SearchItemsQueryHandler[F[_]]:
  def run(input: Input): F[Items]

object SearchItemsQueryHandler:
  case class Input(
      name: Option[String]
  )

  case class Items(
      list: Seq[Item]
  )
  case class Item(
      id: UUID,
      name: String
  )
