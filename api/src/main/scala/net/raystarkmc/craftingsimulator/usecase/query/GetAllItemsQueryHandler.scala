package net.raystarkmc.craftingsimulator.usecase.query

import net.raystarkmc.craftingsimulator.usecase.query.GetAllItemsQueryHandler.AllItems
import java.util.UUID

trait GetAllItemsQueryHandler[F[_]]:
  def run(): F[AllItems]

object GetAllItemsQueryHandler:
  case class AllItems(
    list: Seq[Item]
  )
  case class Item(
    id: UUID,
    name: String,
  )
