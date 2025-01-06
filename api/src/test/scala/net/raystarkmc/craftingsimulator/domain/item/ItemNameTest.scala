package net.raystarkmc.craftingsimulator.domain.item

import cats.instances.all.given
import cats.syntax.all.given
import org.scalatest.freespec.AnyFreeSpec

class ItemNameTest extends AnyFreeSpec:
  "制御文字が含まれる場合失敗" in:
    val expected = Left(ItemName.Error.ContainsControlCharacter).withRight[ItemName]
    val actual = ItemName.either("a\t")
    assert(expected eqv actual)

  "空文字列の場合失敗" in:
    val expected = Left(ItemName.Error.IsBlank).withRight[ItemName]
    val actual = ItemName.either("")
    assert(expected eqv actual)

  "空白のみの場合失敗" in:
    val expected = Left(ItemName.Error.IsBlank).withRight[ItemName]
    val actual = ItemName.either(" ")
    assert(expected eqv actual)

  "全角空白のみの場合失敗" in:
    val expected = Left(ItemName.Error.IsBlank).withRight[ItemName]
    val actual = ItemName.either("　")
    assert(expected eqv actual)

  "日本語で生成" in:
    val expected = Right("サンプルアイテム").withLeft[ItemName.Error]
    val actual = ItemName.either("サンプルアイテム").map(_.value)
    assert(expected eqv actual)

  "空文字列を含んだ日本語で生成" in:
    val expected = Right("サンプル アイテム").withLeft[ItemName.Error]
    val actual = ItemName.either("サンプル アイテム").map(_.value)
    assert(expected eqv actual)