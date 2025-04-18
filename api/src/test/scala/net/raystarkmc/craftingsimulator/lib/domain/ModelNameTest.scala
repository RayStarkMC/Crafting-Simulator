package net.raystarkmc.craftingsimulator.lib.domain

import org.scalatest.freespec.AnyFreeSpec
import cats.syntax.all.given
import cats.instances.all.given
import cats.data.*
import cats.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.Empty

sealed trait TestContext
type TestContextName = TestContextName.T
object TestContextName extends ModelName[TestContext]

import TestContextName.*

class ModelNameTest extends AnyFreeSpec:
  "制御文字が含まれる場合失敗" in :
    val expected =
      TestContextName.Failure.ContainsControlCharacter.asLeft[TestContextName].toEitherNec
    val actual = TestContextName.ae("a\t")

    assert(expected eqv actual)

  "空文字列の場合失敗" in :
    val expected = TestContextName.Failure.IsBlank.asLeft[TestContextName].toEitherNec
    val actual = TestContextName.ae("")
    assert(expected eqv actual)

  "空白のみの場合失敗" in :
    val expected = TestContextName.Failure.IsBlank.asLeft[TestContextName].toEitherNec
    val actual = TestContextName.ae(" ")
    assert(expected eqv actual)

  "全角空白のみの場合失敗" in :
    val expected = TestContextName.Failure.IsBlank.asLeft[TestContextName].toEitherNec
    val actual = TestContextName.ae("　")
    assert(expected eqv actual)

  "日本語で生成" in :
    val expected = "サンプルアイテム".asRight[TestContextName.Failure].toEitherNec
    val actual = TestContextName.ae("サンプルアイテム")
    assert(expected eqv actual)

  "空文字列を含んだ日本語で生成" in :
    val expected = "サンプル アイテム".asRight[TestContextName.Failure].toEitherNec
    val actual = TestContextName.ae("サンプル アイテム")
    assert(expected eqv actual)
