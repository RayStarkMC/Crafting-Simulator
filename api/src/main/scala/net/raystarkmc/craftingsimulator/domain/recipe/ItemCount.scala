package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.data.*
import cats.implicits.*
import cats.derived.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type ItemCount = ItemCount.T
object ItemCount extends RefinedType[Long, Greater[0]]:
  enum Failure derives Hash, Show:
    case IsNotGreaterThen0

  type AE[F[_]] = ApplicativeError[F, NonEmptyChain[Failure]]

  def ae[F[_] : AE as F](value: Long): F[ItemCount] =
    val checkGreaterThan0 = F.fromOption(
      value.refineOption[Greater[0]],
      NonEmptyChain.one(Failure.IsNotGreaterThen0)
    )
    ItemCount.assume(value).pure[F]
      <* checkGreaterThan0