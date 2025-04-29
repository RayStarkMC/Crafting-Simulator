package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.implicits.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName

opaque type ItemName = ModelName
object ItemName extends ItemNameGivens:
  extension (self: ItemName)
    def value: ModelName = self
    
  def apply(value: ModelName): ItemName = value

private inline def wrapItemNameF[F[_]](f: F[ModelName]): F[ItemName] = f

private trait ItemNameGivens:
  given Eq[ItemName] = wrapItemNameF(Eq[ModelName])
  given Hash[ItemName] = wrapItemNameF(Hash[ModelName])
  given Order[ItemName] = wrapItemNameF(Order[ModelName])
  given Show[ItemName] = wrapItemNameF(Show[ModelName])