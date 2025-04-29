package net.raystarkmc.craftingsimulator.lib.domain

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.*
import cats.derived.*
import net.raystarkmc.craftingsimulator.lib.cats.*

opaque type ModelName = String
object ModelName extends ModelNameGivens:
  extension (self: ModelName) def value: String = self

  enum Failure derives Eq, Hash, Show, Order:
    case IsBlank extends Failure
    case LengthExceeded extends Failure
    case ContainsControlCharacter extends Failure

  def ae[
      F[_]: ApplicativeErrorWithNec[Failure] as F
  ](value: String): F[ModelName] =

    val pattern = ".*\\p{Cntrl}.*".r
    val maxLength = 100

    F.pure(value)
      <* F.raiseWhen(value.isBlank)(NonEmptyChain.one(Failure.IsBlank))
      <* F.raiseWhen(value.length > maxLength)(NonEmptyChain.one(Failure.LengthExceeded))
      <* F.raiseWhen(pattern.findFirstIn(value).nonEmpty)(NonEmptyChain.one(Failure.ContainsControlCharacter))

  def inParallel[
      M[_]: {Parallel as P, MonadErrorWithNec[Failure]}
  ](value: String): M[ModelName] =
    given ApplicativeError[P.F, NonEmptyChain[Failure]] = P.applicativeError
    P.sequential(ae(value))

private inline def wrapModelNameF[F[_]](f: F[String]): F[ModelName] = f

private trait ModelNameGivens:
  given Eq[ModelName] = wrapModelNameF(Eq[String])
  given Hash[ModelName] = wrapModelNameF(Hash[String])
  given Order[ModelName] = wrapModelNameF(Order[String])
  given Show[ModelName] = wrapModelNameF(Show[String])