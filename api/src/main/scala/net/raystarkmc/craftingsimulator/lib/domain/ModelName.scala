package net.raystarkmc.craftingsimulator.lib.domain

import cats.data.NonEmptyChain
import cats.derived.*
import cats.syntax.all.given
import cats.{ApplicativeError, Hash, Show}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type IncludeCntrlPattern = ".*\\p{Cntrl}.*"

type ModelNameConstraint =
  Not[Blank] & MaxLength[100] & Not[Match[IncludeCntrlPattern]]

trait ModelName[C] extends RefinedType[String, ModelNameConstraint]:
  enum Failure derives Hash, Show:
    case IsBlank extends Failure
    case LengthExceeded extends Failure
    case ContainsControlCharacter extends Failure

  def ae[F[_]](
      value: String
  )(using F: ApplicativeError[F, NonEmptyChain[Failure]]): F[T] =
    val checkBlank = F.fromOption(
      value.refineOption[Not[Blank]],
      NonEmptyChain.one(Failure.IsBlank)
    )

    val checkMaxLength = F.fromOption(
      value.refineOption[MaxLength[100]],
      NonEmptyChain.one(Failure.LengthExceeded)
    )

    val checkControlCharacter = F.fromOption(
      value.refineOption[Not[Match[IncludeCntrlPattern]]],
      NonEmptyChain.one(Failure.ContainsControlCharacter)
    )

    assume(value).pure[F]
      <* checkBlank
      <* checkMaxLength
      <* checkControlCharacter
