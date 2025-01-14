package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.table

import java.util.UUID

case class ItemTableRecord(
    id: UUID,
    name: String
)
