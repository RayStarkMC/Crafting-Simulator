package net.raystarkmc.craftingsimulator.usecase.command

import cats.instances.all.given
import cats.syntax.all.given
import org.scalatest.freespec.AnyFreeSpec
import cats.data.State
import net.raystarkmc.craftingsimulator.domain.item.Item
import net.raystarkmc.craftingsimulator.domain.item.ItemRepository
import net.raystarkmc.craftingsimulator.domain.item.ItemId
import java.util.UUID
import cats.syntax.set
import cats.effect.std.UUIDGen
import cats.derived.auto.pure
import net.raystarkmc.craftingsimulator.domain.item.ItemName
import cats.kernel.Eq
import cats.kernel.instances.TupleOrderInstances

class RegisterItemCommandHandlerTest extends AnyFreeSpec:
  "名前が不正な場合エラーが返される" in:
    val testUUID = UUID.randomUUID().nn
    type TestState[A] = State[Option[Item], A]
    given ItemRepository[TestState] with
      def resolveById(itemId: ItemId): TestState[Option[Item]] =
        if itemId.value eqv testUUID then State.get else fail()
      def save(item: Item): TestState[Unit] =
        State.set(item.some)

    given UUIDGen[TestState] with
      def randomUUID: TestState[UUID] = testUUID.pure

    val handler = summon[RegisterItemCommandHandler[TestState]]

    val initialState = None

    val result = handler
      .run(
        RegisterItemCommandHandler.Command(name = "")
      )
      .run(initialState)
      .value
    val expected = (
      Option.empty[Item],
      RegisterItemCommandHandler
        .Error(detail = ItemName.Error.IsBlank)
        .asLeft[RegisterItemCommandHandler.Output]
    )

    assert(expected eqv result)

  "Itemを作成して登録する" in:
    val testUUID = UUID.randomUUID().nn
    type TestState[A] = State[Option[Item], A]
    given ItemRepository[TestState] with
      def resolveById(itemId: ItemId): TestState[Option[Item]] =
        if itemId.value eqv testUUID then State.get else fail()
      def save(item: Item): TestState[Unit] =
        State.set(Some(item))

    given UUIDGen[TestState] with
      def randomUUID: TestState[UUID] = testUUID.pure

    val handler = summon[RegisterItemCommandHandler[TestState]]

    val initialState = None

    val result = handler
      .run(
        RegisterItemCommandHandler.Command(name = "item")
      )
      .run(initialState)
      .value

    val expected = (
      Item
        .restore(
          data = Item.Data(
            id = ItemId(testUUID),
            name = ItemName.either("item").getOrElse(fail())
          )
        )
        .some,
      RegisterItemCommandHandler
        .Output(id = testUUID)
        .asRight[RegisterItemCommandHandler.Error]
    )

    assert(expected eqv result)
