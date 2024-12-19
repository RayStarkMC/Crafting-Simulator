package net.raystarkmc.craftingsimulator.domain.item

import cats.{Hash, Show}
import cats.derived.*

opaque type ItemName = String

object ItemName:
  enum Error derives Hash, Show:
    case IsBlank
    case ContainsControlCharacter

  extension (self: ItemName)
    def value: String = self

  def either(value: String): Either[Error, ItemName] =
    if value.isBlank then Left(Error.IsBlank)
    else if "\\p{Cntrl}".r.findFirstIn(value).isDefined then Left(Error.ContainsControlCharacter)
    else Right(value)

object ItemNameGivens:
  given Hash[ItemName] = Hash.by(_.value)
  given Show[ItemName] = Show.show(_.value)

