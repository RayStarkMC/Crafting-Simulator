package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
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

trait RegisterItemCommandHandler[F[_]]:
  def run(command: Command): F[Either[RegisterItemCommandHandler.Error, Output]]

object RegisterItemCommandHandler extends RegisterItemCommandHandlerGivens:
  case class Command(name: String) derives Hash, Show
  case class Output(id: UUID) derives Hash, Show
  case class Error(detail: ItemName.Error) derives Hash, Show

trait RegisterItemCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, ItemRepository}] => RegisterItemCommandHandler[F] =
    object instance extends RegisterItemCommandHandler[F]:
      private val itemRepository: ItemRepository[F] = summon

      def run(
          command: Command
      ): F[Either[RegisterItemCommandHandler.Error, Output]] =
        val eitherT = for {
          name <- EitherT.fromEither[F](
            ItemName
              .ae(command.name)
              .leftMap(RegisterItemCommandHandler.Error.apply)
          )
          item <- EitherT.liftF(
            Item.create(name)
          )
          _ <- EitherT.liftF(
            itemRepository.save(item)
          )
        } yield Output(item.data.id.unwrap)
        eitherT.value
    instance
