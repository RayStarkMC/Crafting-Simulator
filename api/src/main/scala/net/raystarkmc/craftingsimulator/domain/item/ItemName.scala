package net.raystarkmc.craftingsimulator.domain.item

opaque type ItemName = String

object ItemName:
  def either(value: String): Either[Error, ItemName] =
    if value.isBlank then Left(Error.IsBlank)
    else if "\\p{Cntrl}".r.findFirstIn(value).isDefined then Left(Error.ContainsControlCharacter)
    else Right(value)

  enum Error:
    case IsBlank
    case ContainsControlCharacter
