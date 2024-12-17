package net.raystarkmc.craftingsimulator.domain.item

import org.scalatest.freespec.AnyFreeSpec

import java.util.UUID

class ItemTest extends AnyFreeSpec {
  val testingUUID: UUID = UUID.randomUUID().asInstanceOf[UUID]

  "復元する" in {
    val expected = Item.Data(
      id = ItemId(testingUUID),
      name = ItemName.either("item").getOrElse(fail())
    )
    val actual = Item.restore(
      Item.Data(
        id = ItemId(testingUUID),
        name = ItemName.either("item").getOrElse(fail())
      )
    )
    assert(expected === actual)
  }

  "名前を変更する" in {
    val item = Item.restore(
      Item.Data(
        id = ItemId(testingUUID),
        name = ItemName.either("item").getOrElse(fail())
      )
    )
    val expected = Item.restore(
      Item.Data(
        id = ItemId(testingUUID),
        name = ItemName.either("newItem").getOrElse(fail())
      )
    )
    val actual = item.update(
      ItemName.either("newItem").getOrElse(fail())
    )
    assert(expected === actual)
  }
}
