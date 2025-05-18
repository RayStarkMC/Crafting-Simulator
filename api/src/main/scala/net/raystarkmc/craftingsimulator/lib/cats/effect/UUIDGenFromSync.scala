package net.raystarkmc.craftingsimulator.lib.cats.effect

import cats.effect.*
import cats.effect.std.*
import java.util.UUID

/** @see
  *   cats.effect.std.UUIDGenCompanionPlatformLowPriority.fromSync
  */
trait UUIDGenFromSync:
  given [F[_]: Sync as syncF] => UUIDGen[F]:
    val randomUUID: F[UUID] = syncF.blocking(UUID.randomUUID())

object UUIDGenFromSync extends UUIDGenFromSync
