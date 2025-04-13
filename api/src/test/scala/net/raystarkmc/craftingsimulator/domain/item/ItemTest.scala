package net.raystarkmc.craftingsimulator.domain.item

import org.scalatest.freespec.AnyFreeSpec
import io.github.iltotore.iron.*

import java.util.UUID

class ItemTest extends AnyFreeSpec {
  val testingUUID: UUID = UUID.randomUUID().nn

  "名前を変更する" in {
    val item = Item.restore(
      id = ItemId(testingUUID),
      name = ItemName.ae("item").getOrElse(fail())
    )
    val expected = Item.restore(
      id = ItemId(testingUUID),
      name = ItemName.ae("newItem").getOrElse(fail())
    )
    val actual = item.update(
      ItemName.ae("newItem").getOrElse(fail())
    )
    assert(expected === actual)
  }
}
