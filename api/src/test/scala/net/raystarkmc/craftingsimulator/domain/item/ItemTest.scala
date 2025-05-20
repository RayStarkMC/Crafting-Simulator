package net.raystarkmc.craftingsimulator.domain.item

import net.raystarkmc.craftingsimulator.lib.domain.ModelName
import org.scalatest.freespec.AnyFreeSpec

import java.util.UUID

class ItemTest extends AnyFreeSpec {
  val testingUUID: UUID = UUID.randomUUID().nn

  "名前を変更する" in {
    val item = Item.restore(
      id = ItemId(testingUUID),
      name = ModelName.ae("item").map(ItemName.apply).getOrElse(fail())
    )
    val expected = Item.restore(
      id = ItemId(testingUUID),
      name = ModelName.ae("newItem").map(ItemName.apply).getOrElse(fail())
    )
    val actual = item.update(
      ModelName.ae("newItem").map(ItemName.apply).getOrElse(fail())
    )
    assert(expected === actual)
  }

  //FIXME: CI検証終わったら削除
  "失敗させる" in {
    fail("Oops!")
  }
}
