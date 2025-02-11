package net.raystarkmc.craftingsimulator.usecase.command

import cats.data.State
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.item.{
  Item,
  ItemId,
  ItemName,
  ItemRepository
}
import org.scalatest.freespec.AnyFreeSpec

import java.util.UUID

class RegisterItemCommandHandlerTest extends AnyFreeSpec:
  "名前が不正な場合エラーが返される" in:
    val testUUID = UUID.randomUUID().nn
    type TestState[A] = State[Option[Item], A]
    given ItemRepository[TestState]:
      def resolveById(itemId: ItemId): TestState[Option[Item]] =
        if itemId.value eqv testUUID then State.get else fail()
      def save(item: Item): TestState[Unit] =
        State.set(item.some)

    given UUIDGen[TestState]:
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
    given ItemRepository[TestState]:
      def resolveById(itemId: ItemId): TestState[Option[Item]] =
        if itemId.value eqv testUUID then State.get else fail()
      def save(item: Item): TestState[Unit] =
        State.set(Some(item))

    given UUIDGen[TestState]:
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
            name = ItemName.ae("item").getOrElse(fail())
          )
        )
        .some,
      RegisterItemCommandHandler
        .Output(id = testUUID)
        .asRight[RegisterItemCommandHandler.Error]
    )

    assert(expected eqv result)
