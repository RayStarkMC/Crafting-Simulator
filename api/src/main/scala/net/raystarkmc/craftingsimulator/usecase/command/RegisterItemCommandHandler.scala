package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import net.raystarkmc.craftingsimulator.domain.item.{
  Item,
  ItemName,
  ItemRepository
}
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler.{
  Command,
  Output
}

import java.util.UUID

trait RegisterItemCommandHandler[F[_]: Monad: UUIDGen: ItemRepository]:
  private val itemRepository: ItemRepository[F] = summon

  def run(
      command: Command
  ): F[Either[RegisterItemCommandHandler.Error, Output]] =
    val eitherT = for {
      name <- EitherT.fromEither[F](
        ItemName
          .either(command.name)
          .left
          .map(RegisterItemCommandHandler.Error.apply)
      )
      item <- EitherT.liftF(
        Item.create(name)
      )
      _ <- EitherT.liftF(
        itemRepository.save(item)
      )
    } yield Output(item.data.id.value)

    eitherT.value

object RegisterItemCommandHandler extends RegisterItemCommandHandlerGivens:
  case class Command(name: String) derives Hash, Show
  case class Output(id: UUID) derives Hash, Show
  case class Error(detail: ItemName.Error) derives Hash, Show

trait RegisterItemCommandHandlerGivens:
  given [F[_]: Monad: UUIDGen: ItemRepository]: RegisterItemCommandHandler[F] =
    object command extends RegisterItemCommandHandler[F]
    command
