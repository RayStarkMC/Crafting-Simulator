package net.raystarkmc.craftingsimulator.lib.domain

import cats.data.NonEmptyChain
import cats.derived.*
import cats.syntax.all.given
import cats.{ApplicativeError, Hash, Show}

opaque type ModelName[C] <: String = String

private inline def unwrapModelName[C](name: ModelName[C]): String = name
private inline def wrapModelName[C](value: String): ModelName[C] = value

trait ModelNameSyntax[C]:
  extension (self: ModelName[C]) def unwrap: String = unwrapModelName(self)

  given Hash[ModelName[C]] = Hash.by(_.unwrap)
  given Show[ModelName[C]] = Show.show(_.unwrap.show)

  enum Failure derives Hash, Show:
    case IsBlank extends Failure
    case LengthExceeded extends Failure
    case ContainsControlCharacter extends Failure

  def ae[F[_]](
      value: String
  )(using F: ApplicativeError[F, NonEmptyChain[Failure]]): F[ModelName[C]] =
    F.raiseWhen(value.isBlank)(NonEmptyChain.one(Failure.IsBlank))
      *> F.raiseWhen(value.length > 100)(
        NonEmptyChain.one(Failure.LengthExceeded)
      )
      *> F.raiseWhen("\\p{Cntrl}".r.findFirstIn(value).isDefined)(
        NonEmptyChain.one(Failure.ContainsControlCharacter)
      )
      *> wrapModelName(value).pure[F]
