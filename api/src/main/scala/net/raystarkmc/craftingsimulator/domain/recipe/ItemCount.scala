package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.data.*
import cats.syntax.all.*
import cats.instances.all.given
import cats.derived.*

opaque type ItemCount = Long
object ItemCount extends ItemCountGivens:
  extension (self: ItemCount)
    def value: Long = self

  enum Failure derives Eq, Hash, Show:
    case IsNotGreaterThen0

  type AE[F[_]] = ApplicativeError[F, NonEmptyChain[Failure]]

  def ae[F[_] : AE as F](value: Long): F[ItemCount] = {
    F.raiseWhen(value <= 0)(NonEmptyChain.one(Failure.IsNotGreaterThen0)) *>
      F.pure(value)
  }

private inline def wrapItemCountF[F[_]](f: F[Long]): F[ItemCount] = f

trait ItemCountGivens:
  given Eq[ItemCount] = wrapItemCountF(Eq[Long])
  given Hash[ItemCount] = wrapItemCountF(Hash[Long])
  given Order[ItemCount] = wrapItemCountF(Order[Long])
  given Show[ItemCount] = wrapItemCountF(Show[Long])