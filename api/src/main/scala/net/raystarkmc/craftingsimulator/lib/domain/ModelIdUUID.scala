package net.raystarkmc.craftingsimulator.lib.domain

import cats.effect.std.UUIDGen
import cats.syntax.all.given
import cats.{Hash, Show}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}

import java.util.UUID

opaque type ModelIdUUID[C] = UUID :| Pure

private inline def wrapFModelIdUUID[F[_], C](uuidF: F[UUID]): F[ModelIdUUID[C]] = uuidF.assumeAll // Pure制約のため

trait ModelIdUUIDTypeOps[C] extends RefinedTypeOps[UUID, Pure, ModelIdUUID[C]]:
  def generate[F[_]: UUIDGen]: F[ModelIdUUID[C]] =
    wrapFModelIdUUID(UUIDGen.randomUUID)
    
  protected val hash: Hash[ModelIdUUID[C]] = Hash[ModelIdUUID[C]]
  protected val show: Show[ModelIdUUID[C]] = Show[ModelIdUUID[C]]
