package net.raystarkmc.craftingsimulator.lib.domain

import cats.*
import cats.effect.std.UUIDGen
import io.github.iltotore.iron.*

import java.util.UUID

trait ModelIdUUID[C] extends RefinedType[UUID, Pure]:
  def generate[F[_]: UUIDGen]: F[T] =
    assumeAll(UUIDGen.randomUUID)
