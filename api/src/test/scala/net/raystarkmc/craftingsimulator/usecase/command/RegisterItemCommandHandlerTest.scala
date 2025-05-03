package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.data.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.command.item.RegisterItemCommandHandler
import org.scalatest.freespec.AnyFreeSpec

import java.util.UUID

class RegisterItemCommandHandlerTest extends AnyFreeSpec:
  "名前が不正な場合エラーが返される" in:
    val testUUID: UUID = UUID.randomUUID().nn
    type MockDB[A] = State[Option[Item], A]
    given ItemRepository[MockDB]:
      def resolveById(itemId: ItemId): MockDB[Option[Item]] =
        if itemId.value eqv testUUID then State.get else fail()
      def save(item: Item): MockDB[Unit] =
        State.set(item.some)
      def delete(item: Item): MockDB[Unit] =
        fail()

    given UUIDGen[MockDB]:
      def randomUUID: MockDB[UUID] = testUUID.pure
      
    given Transaction[MockDB, MockDB] = Transaction.noop
        
    val handler = summon[RegisterItemCommandHandler[MockDB]]

    val initialState = None

    val (state, output) = handler
      .run(
        RegisterItemCommandHandler.Command(name = "")
      )
      .run(initialState)
      .value

    assert(state.isEmpty)
    assert(output.isLeft)

  "Itemを作成して登録する" in:
    val testUUID = UUID.randomUUID().nn
    type MockDB[A] = State[Option[Item], A]
    given ItemRepository[MockDB]:
      def resolveById(itemId: ItemId): MockDB[Option[Item]] =
        if itemId.value eqv testUUID then State.get else fail()
      def save(item: Item): MockDB[Unit] =
        State.set(Some(item))
      def delete(item: Item): MockDB[Unit] =
        fail()

    given UUIDGen[MockDB]:
      def randomUUID: MockDB[UUID] = testUUID.pure

    given Transaction[MockDB, MockDB] = Transaction.noop

    val handler = summon[RegisterItemCommandHandler[MockDB]]

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
          id = ItemId(testUUID),
          name = ModelName.ae("item").map(ItemName.apply).getOrElse(fail())
        )
        .some,
      RegisterItemCommandHandler
        .Output(id = testUUID)
        .asRight[RegisterItemCommandHandler.Failure]
    )

    assert(expected.eqv(result))
