package net.raystarkmc.craftingsimulator.lib.domain

import cats.*
import cats.effect.std.UUIDGen
import cats.implicits.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given

import java.util.UUID

trait ModelIdUUID[C] extends RefinedType[UUID, Pure]:
  def generate[F[_]: UUIDGen]: F[T] =
    assumeAll(UUIDGen.randomUUID)

  protected val hash: Hash[T] = summon
  protected val show: Show[T] = summon
