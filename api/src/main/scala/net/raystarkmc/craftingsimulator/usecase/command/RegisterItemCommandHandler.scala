package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.domain.item.*
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
  case class Error(detail: ItemName.Failure) derives Hash, Show

trait RegisterItemCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, ItemRepository}]
    => RegisterItemCommandHandler[F] =
    object instance extends RegisterItemCommandHandler[F]:
      private val itemRepository: ItemRepository[F] = summon

      def run(
          command: Command
      ): F[Either[RegisterItemCommandHandler.Error, Output]] =
        val eitherT = for {
          name <- ItemName
            .ae(command.name)
            .leftMap(_.head)
            .leftMap(RegisterItemCommandHandler.Error.apply)
            .toEitherT[F]
          item <- EitherT.liftF(
            Item.create(name)
          )
          _ <- EitherT.liftF(
            itemRepository.save(item)
          )
        } yield Output(item.id.value)
        eitherT.value
    instance
