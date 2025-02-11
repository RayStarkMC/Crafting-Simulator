package net.raystarkmc.craftingsimulator.domain.item

import cats.{ApplicativeError, Hash, Show}
import cats.derived.*
import cats.syntax.all.given

opaque type ItemName = String

object ItemName extends ItemNameGivens:
  enum Error derives Hash, Show:
    case IsBlank
    case ContainsControlCharacter

  extension (self: ItemName)
    def value: String = self

  def ae[F[_]](value: String)(using F: ApplicativeError[F, Error]): F[ItemName] =
    F.raiseWhen(value.isBlank)(Error.IsBlank)
      *> F.raiseWhen("\\p{Cntrl}".r.findFirstIn(value).isDefined)(Error.ContainsControlCharacter)
      *> value.pure[F]

  def either(value: String): Either[Error, ItemName] = ae(value)

trait ItemNameGivens:
  given Hash[ItemName] = Hash.by(_.value)
  given Show[ItemName] = Show.show(_.value.show)