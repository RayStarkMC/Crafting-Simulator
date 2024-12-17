package net.raystarkmc.craftingsimulator.domain.item

import cats.Eq
import net.raystarkmc.craftingsimulator.domain.item.Item.Data
import net.raystarkmc.craftingsimulator.domain.item.ItemName

opaque type Item = Data

object Item:
  case class Data(
    id: ItemId,
    name: ItemName,
  )
  
  extension (self: Item)
    def data: Data = self
    def update(newName: ItemName): Item =
      self.copy(name = newName)
  
  def restore(data: Data): Item = data
  
  given eqForItemData: Eq[Item.Data] = Eq.fromUniversalEquals
  given eqForItem: Eq[Item] = eqForItemData