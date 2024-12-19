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
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommand.{
  Input,
  Output
}

import java.util.UUID

trait RegisterItemCommand[F[_]: Monad: ItemRepository: UUIDGen]:
  val itemRepository = summon[ItemRepository[F]].mapK(
    EitherT.liftK[F, RegisterItemCommand.Error]
  )

  def run(input: Input): F[Either[RegisterItemCommand.Error, Output]] =
    val eitherT = for {
      name <- EitherT
        .fromEither(ItemName.either(input.name))
        .leftMap(RegisterItemCommand.Error.apply)
      item <- EitherT
        .liftF(Item.create(name))
      _ <- itemRepository.save(item)
    } yield Output(item.data.id.value)

    eitherT.value

object RegisterItemCommand {
  case class Input(name: String) derives Hash, Show
  case class Output(id: UUID) derives Hash, Show
  case class Error(detail: ItemName.Error) derives Hash, Show
}

object RegisterItemCommandGivens {
  given [F[_] : Monad: ItemRepository: UUIDGen]: RegisterItemCommand[F] =
    object command extends RegisterItemCommand[F]
    command
}