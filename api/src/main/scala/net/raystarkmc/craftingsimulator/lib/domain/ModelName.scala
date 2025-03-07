package net.raystarkmc.craftingsimulator.lib.domain

import cats.data.NonEmptyChain
import cats.derived.*
import cats.syntax.all.given
import cats.{ApplicativeError, Hash, Show}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type ModelNameConstraint =
  Not[Blank] & MaxLength[100] & Match["^[^\\p{Cntrl}]*$"]
opaque type ModelName[C] = String :| ModelNameConstraint

private inline def wrapModelName[C](
    value: String :| ModelNameConstraint
): ModelName[C] = value

trait ModelNameTypeOps[C] extends RefinedTypeOps[String, ModelNameConstraint, ModelName[C]]:
  enum Failure derives Hash, Show:
    case IsBlank extends Failure
    case LengthExceeded extends Failure
    case ContainsControlCharacter extends Failure

  def ae[F[_]](
      value: String
  )(using F: ApplicativeError[F, NonEmptyChain[Failure]]): F[ModelName[C]] =
    val checkBlank = F.fromOption(
      value.refineOption[Not[Blank]],
      NonEmptyChain.one(Failure.IsBlank)
    )

    val checkMaxLength = F.fromOption(
      value.refineOption[MaxLength[100]],
      NonEmptyChain.one(Failure.LengthExceeded)
    )

    val checkControlCharacter = F.fromOption(
      value.refineOption[Match["^[^\\p{Cntrl}]*$"]],
      NonEmptyChain.one(Failure.ContainsControlCharacter)
    )

    wrapModelName(value.assume).pure[F]
      <* checkBlank
      <* checkMaxLength
      <* checkControlCharacter
