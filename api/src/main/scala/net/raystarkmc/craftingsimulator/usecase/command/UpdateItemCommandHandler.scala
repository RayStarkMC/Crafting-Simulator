package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName
import net.raystarkmc.craftingsimulator.usecase.command.UpdateItemCommandHandler.*

import java.util.UUID

trait UpdateItemCommandHandler[F[_]]:
  def run(command: Command): F[Either[UpdateItemCommandHandler.Error, Unit]]

object UpdateItemCommandHandler extends UpdateItemCommandHandlerGivens:
  case class Command(id: UUID, name: String) derives Hash, Show
  enum Error derives Hash, Show:
    case NameError(detail: ModelName.Failure)
    case NotFound

trait UpdateItemCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, ItemRepository as itemRepository}] => UpdateItemCommandHandler[F]:
    def run(
        command: Command
    ): F[Either[UpdateItemCommandHandler.Error, Unit]] =
      val eitherT: EitherT[F, UpdateItemCommandHandler.Error, Unit] = for {
        name <- ModelName
          .ae(command.name)
          .map(ItemName.apply)
          .leftMap(_.head)
          .leftMap(UpdateItemCommandHandler.Error.NameError.apply)
          .toEitherT[F]
        itemId = ItemId(command.id)
        item <- EitherT.fromOptionF(
          itemRepository.resolveById(itemId),
          UpdateItemCommandHandler.Error.NotFound
        )
        updated = item.update(name)
        _ <- EitherT.liftF(
          itemRepository.save(updated)
        )
      } yield ()
      eitherT.value
