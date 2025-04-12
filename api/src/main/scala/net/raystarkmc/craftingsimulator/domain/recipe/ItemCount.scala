package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.data.*
import cats.implicits.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

opaque type ItemCount = Long :| Greater[0]
object ItemCount extends RefinedTypeOps[Long, Greater[0], ItemCount]:
  enum Failure:
    case IsNotGreaterThen0

  type AE[F[_]] = ApplicativeError[F, NonEmptyChain[Failure]]

  def ae[F[_] : AE as F](value: Long): F[ItemCount] =
    val checkGreaterThan0 = F.fromOption(
      value.refineOption[Greater[0]],
      NonEmptyChain.one(Failure.IsNotGreaterThen0)
    )
    ItemCount.assume(value).pure[F]
      <* checkGreaterThan0