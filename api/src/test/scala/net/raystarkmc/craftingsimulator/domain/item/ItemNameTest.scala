package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import org.scalatest.freespec.AnyFreeSpec

class ItemNameTest extends AnyFreeSpec:
  "制御文字が含まれる場合失敗" in:
    val expected = ItemName.Error.ContainsControlCharacter.asLeft[ItemName]
    val actual = ItemName.ae("a\t")
    assert(expected eqv actual)

  "空文字列の場合失敗" in:
    val expected = ItemName.Error.IsBlank.asLeft[ItemName]
    val actual = ItemName.ae("")
    assert(expected eqv actual)

  "空白のみの場合失敗" in:
    val expected = ItemName.Error.IsBlank.asLeft[ItemName]
    val actual = ItemName.ae(" ")
    assert(expected eqv actual)

  "全角空白のみの場合失敗" in:
    val expected = ItemName.Error.IsBlank.asLeft[ItemName]
    val actual = ItemName.ae("　")
    assert(expected eqv actual)

  "日本語で生成" in:
    val expected = "サンプルアイテム".asRight[ItemName.Error]
    val actual = ItemName.ae("サンプルアイテム").map(_.value)
    assert(expected eqv actual)

  "空文字列を含んだ日本語で生成" in:
    val expected = "サンプル アイテム".asRight[ItemName.Error]
    val actual = ItemName.ae("サンプル アイテム").map(_.value)
    assert(expected eqv actual)