package net.raystarkmc.craftingsimulator.domain.item

import org.scalatest.freespec.AnyFreeSpec

class ItemNameTest extends AnyFreeSpec:
  "制御文字が含まれる場合失敗" in {
    assertResult(
      Left(ItemName.Error.ContainsControlCharacter)
    ) {
      ItemName.either("a\t")
    }
  }

  "空文字列の場合失敗" in {
    assertResult(
      Left(ItemName.Error.IsBlank)
    ) {
      ItemName.either("")
    }
  }

  "空白のみの場合失敗" in {
    assertResult(
      Left(ItemName.Error.IsBlank)
    ) {
      ItemName.either(" ")
    }
  }

  "全角空白のみの場合失敗" in {
    assertResult(
      Left(ItemName.Error.IsBlank)
    ) {
      ItemName.either("　")
    }
  }


  "日本語で生成" in {
    assertResult(
      Right("サンプルアイテム").withLeft[ItemName.Error]
    ) {
      ItemName.either("サンプルアイテム")
    }
  }

  "空文字列を含んだ日本語で生成" in {
    assertResult(
      Right("サンプル アイテム").withLeft[ItemName.Error]
    ) {
      ItemName.either("サンプル アイテム")
    }
  }