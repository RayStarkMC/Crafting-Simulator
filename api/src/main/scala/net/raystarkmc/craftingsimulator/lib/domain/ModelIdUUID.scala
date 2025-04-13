package net.raystarkmc.craftingsimulator.lib.domain

import cats.*
import cats.effect.std.UUIDGen
import cats.implicits.*
import cats.derived.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.{*, given}

import java.util.UUID

type ModelIdUUID[C] = UUID :| Pure

private inline def wrapFModelIdUUID[F[_], C](uuidF: F[UUID]): F[ModelIdUUID[C]] = uuidF.assumeAll // Pure制約のため

trait ModelIdUUIDTypeOps[C] extends RefinedType[UUID, Pure]:
  def generate[F[_]: UUIDGen]: F[ModelIdUUID[C]] =
    wrapFModelIdUUID(UUIDGen.randomUUID)
    
  protected val hash: Hash[ModelIdUUID[C]] = summon
  protected val show: Show[ModelIdUUID[C]] = summon
