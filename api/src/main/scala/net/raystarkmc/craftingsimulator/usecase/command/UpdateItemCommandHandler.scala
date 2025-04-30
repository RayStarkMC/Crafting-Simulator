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
  def run(command: Command): F[Either[Failure, Unit]]

object UpdateItemCommandHandler extends UpdateItemCommandHandlerGivens:
  case class Command(id: UUID, name: String) derives Hash, Show
  enum Failure derives Hash, Show:
    case ValidationFailed(detail: String)
    case NotFound

trait UpdateItemCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, ItemRepository as itemRepository}] => UpdateItemCommandHandler[F]:
    def run(
        command: Command
    ): F[Either[Failure, Unit]] =
      val itemId = ItemId(command.id)
      val eitherT: EitherT[F, Failure, Unit] = for {
        name <- ModelName
          .inParallel[EitherNec[ModelName.Failure, _]](command.name)
          .leftMap(_.show)
          .map(ItemName.apply)
          .leftMap(Failure.ValidationFailed.apply)
          .toEitherT[F]
        item <- EitherT.fromOptionF(
          itemRepository.resolveById(itemId),
          Failure.NotFound
        )
        updated = item.update(name)
        _ <- EitherT.liftF(
          itemRepository.save(updated)
        )
      } yield ()
      eitherT.value
