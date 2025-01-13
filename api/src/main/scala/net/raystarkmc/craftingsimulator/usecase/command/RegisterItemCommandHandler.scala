package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.data.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import cats.derived.*
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

trait RegisterItemCommandHandler[F[_]: Monad: ItemRepository: UUIDGen]:
  private val itemRepository = summon[ItemRepository[F]]

  def run(command: Command): F[Either[RegisterItemCommandHandler.Error, Output]] =
    val eitherT = for {
      name <- EitherT
        .fromEither(ItemName.either(command.name))
        .leftMap(RegisterItemCommandHandler.Error.apply)
      item <- EitherT
        .liftF(Item.create(name))
      _ <- EitherT.right(itemRepository.save(item))
    } yield Output(item.data.id.value)

    eitherT.value

object RegisterItemCommandHandler extends RegisterItemCommandHandlerGivens:
  case class Command(name: String) derives Hash, Show
  case class Output(id: UUID) derives Hash, Show
  case class Error(detail: ItemName.Error) derives Hash, Show

trait RegisterItemCommandHandlerGivens:
  given [F[_] : Monad: ItemRepository: UUIDGen]: RegisterItemCommandHandler[F] =
    object command extends RegisterItemCommandHandler[F]
    command