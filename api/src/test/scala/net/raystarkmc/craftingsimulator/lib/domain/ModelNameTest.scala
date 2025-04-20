package net.raystarkmc.craftingsimulator.lib.domain

import org.scalatest.freespec.AnyFreeSpec
import cats.*
import cats.implicits.*
import cats.data.*

class ModelNameTest extends AnyFreeSpec:
  type F[A] = EitherNec[ModelName.Failure, A]

  "制御文字が含まれる場合失敗" in :
    val expected =
      ModelName.Failure.ContainsControlCharacter.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F]("a\t")

    assert(expected eqv actual)

  "空文字列の場合失敗" in :
    val expected = ModelName.Failure.IsBlank.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F]("")
    assert(expected eqv actual)

  "空白のみの場合失敗" in :
    val expected = ModelName.Failure.IsBlank.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F](" ")
    assert(expected eqv actual)

  "全角空白のみの場合失敗" in :
    val expected = ModelName.Failure.IsBlank.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F]("　")
    assert(expected eqv actual)

  "日本語で生成" in :
    val expected = "サンプルアイテム"
    val actual = ModelName.ae[F]("サンプルアイテム")

    assert {
      expected eqv actual.getOrElse[ModelName](fail()).value
    }

  "空文字列を含んだ日本語で生成" in :
    val expected = "サンプル アイテム"
    val actual = ModelName.ae("サンプル アイテム")
    assert {
      expected eqv actual.getOrElse[ModelName](fail()).value
    }
