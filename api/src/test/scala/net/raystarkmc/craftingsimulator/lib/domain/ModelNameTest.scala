package net.raystarkmc.craftingsimulator.lib.domain

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.lib.cats.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName.Failure
import org.scalatest.freespec.AnyFreeSpec

class ModelNameTest extends AnyFreeSpec:
  type F[A] = EitherNec[Failure, A]

  "空文字列の場合失敗" in:
    val expected = Failure.IsBlank.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F]("")
    assert(expected eqv actual)

  "制御文字が含まれる場合失敗" in:
    val expected =
      Failure.ContainsControlCharacter.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F]("a\t")

    assert(expected eqv actual)

  "空白のみの場合失敗" in:
    val expected = Failure.IsBlank.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F](" ")
    assert(expected eqv actual)

  "全角空白のみの場合失敗" in:
    val expected = Failure.IsBlank.asLeft[ModelName].toEitherNec
    val actual = ModelName.ae[F]("　")
    assert(expected eqv actual)

  "日本語で生成" in:
    val expected = "サンプルアイテム"
    val actual = ModelName.ae[F]("サンプルアイテム")

    assert {
      expected eqv actual.getOrElse[ModelName](fail()).value
    }

  "空文字列を含んだ日本語で生成" in:
    val expected = "サンプル アイテム"
    val actual = ModelName.ae("サンプル アイテム")
    assert {
      expected eqv actual.getOrElse[ModelName](fail()).value
    }

  "inParallelにより長さ超過と制御文字列含みのエラーがまとめて返される" in:
    val expected = NonEmptyChain(
      Failure.LengthExceeded,
      Failure.ContainsControlCharacter,
    )
    val actual = ModelName.inParallel[EitherNec[Failure, _]](
      "\naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    )

    assert {
      expected eqv actual.swap.getOrElse[NonEmptyChain[Failure]](fail()).sorted
    }
