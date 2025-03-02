package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.item.{
  ItemId,
  ItemName,
  ItemRepository
}
import net.raystarkmc.craftingsimulator.usecase.command.UpdateItemCommandHandler.{
  Command,
  Output
}

import java.util.UUID

trait UpdateItemCommandHandler[F[_]]:
  def run(command: Command): F[Either[UpdateItemCommandHandler.Error, Output]]

object UpdateItemCommandHandler extends UpdateItemCommandHandlerGivens:
  case class Command(id: UUID, name: String) derives Hash, Show
  case class Output() derives Hash, Show
  enum Error derives Hash, Show:
    case NameError(detail: ItemName.Error)
    case NotFound

trait UpdateItemCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, ItemRepository}]
    => UpdateItemCommandHandler[F] =
    object instance extends UpdateItemCommandHandler[F]:
      private val itemRepository: ItemRepository[F] = summon

      def run(
          command: Command
      ): F[Either[UpdateItemCommandHandler.Error, Output]] =
        val eitherT: EitherT[F, UpdateItemCommandHandler.Error, Output] = for {
          name <- EitherT.fromEither[F](
            ItemName
              .ae(command.name)
              .leftMap(UpdateItemCommandHandler.Error.NameError.apply)
          )
          itemId = ItemId(command.id)
          itemOption <- EitherT.liftF(
            itemRepository.resolveById(itemId)
          )
          item <- EitherT.fromOption(
            itemOption,
            UpdateItemCommandHandler.Error.NotFound
          )
          updated = item.update(name)
          _ <- EitherT.liftF(
            itemRepository.save(updated)
          )
        } yield Output()
        eitherT.value
    instance
