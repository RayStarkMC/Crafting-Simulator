package net.raystarkmc.craftingsimulator.lib.domain

import cats.effect.std.UUIDGen
import cats.syntax.all.given
import cats.{Hash, Show}
import io.github.iltotore.iron.*

import java.util.UUID

opaque type ModelIdUUID[C] <: UUID = UUID :| Pure

private inline def unwrapModelIdUUID[C](id: ModelIdUUID[C]): UUID = id
private inline def wrapFModelIdUUID[F[_], C](uuidF: F[UUID]): F[ModelIdUUID[C]] = uuidF.asInstanceOf

trait ModelIdUUIDTypeOps[C] extends RefinedTypeOps[UUID, Pure, ModelIdUUID[C]]:
  extension (self: ModelIdUUID[C]) def unwrap: UUID = unwrapModelIdUUID(self)

  def generate[F[_]: UUIDGen]: F[ModelIdUUID[C]] =
    wrapFModelIdUUID(UUIDGen.randomUUID)

  given Hash[ModelIdUUID[C]] = Hash.by(_.unwrap)
  given Show[ModelIdUUID[C]] = Show.show(_.unwrap.show)
