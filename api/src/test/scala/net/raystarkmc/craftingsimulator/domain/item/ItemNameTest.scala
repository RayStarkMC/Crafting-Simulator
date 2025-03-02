package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import org.scalatest.freespec.AnyFreeSpec
import net.raystarkmc.craftingsimulator.domain.item.ItemName
import net.raystarkmc.craftingsimulator.domain.item.ItemName.given
import net.raystarkmc.craftingsimulator.domain.item.ItemName.*

class ItemNameTest extends AnyFreeSpec:
  "制御文字が含まれる場合失敗" in:
    val expected =
      ItemName.Failure.ContainsControlCharacter.asLeft[ItemName].toEitherNec
    val actual = ItemName.ae("a\t")
    assert(expected eqv actual)

  "空文字列の場合失敗" in:
    val expected = ItemName.Failure.IsBlank.asLeft[ItemName].toEitherNec
    val actual = ItemName.ae("")
    assert(expected eqv actual)

  "空白のみの場合失敗" in:
    val expected = ItemName.Failure.IsBlank.asLeft[ItemName].toEitherNec
    val actual = ItemName.ae(" ")
    assert(expected eqv actual)

  "全角空白のみの場合失敗" in:
    val expected = ItemName.Failure.IsBlank.asLeft[ItemName].toEitherNec
    val actual = ItemName.ae("　")
    assert(expected eqv actual)

  "日本語で生成" in:
    val expected = "サンプルアイテム".asRight[ItemName.Failure].toEitherNec
    val actual = ItemName.ae("サンプルアイテム").map(_.unwrap)
    assert(expected eqv actual)

  "空文字列を含んだ日本語で生成" in:
    val expected = "サンプル アイテム".asRight[ItemName.Failure].toEitherNec
    val actual = ItemName.ae("サンプル アイテム").map(_.unwrap)
    assert(expected eqv actual)
