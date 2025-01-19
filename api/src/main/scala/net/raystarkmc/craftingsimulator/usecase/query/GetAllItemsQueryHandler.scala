package net.raystarkmc.craftingsimulator.usecase.query

import net.raystarkmc.craftingsimulator.usecase.query.GetAllItemsQueryHandler.AllItems

trait GetAllItemsQueryHandler[F[_]]:
  def run(): F[AllItems]

object GetAllItemsQueryHandler:
  case class Query()
  case class AllItems(
    list: Seq[Item]
  )
  case class Item(
    id: String,
    name: String,
  )
