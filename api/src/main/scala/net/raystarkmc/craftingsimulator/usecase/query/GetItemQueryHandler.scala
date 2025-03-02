package net.raystarkmc.craftingsimulator.usecase.query

import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler.*

import java.util.UUID

trait GetItemQueryHandler[F[_]]:
  def run(input: Input): F[Option[Item]]

object GetItemQueryHandler:
  case class Input(
    id: UUID,
  )

  case class Item(
    id: UUID,
    name: String
  )