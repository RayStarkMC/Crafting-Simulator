package net.raystarkmc.craftingsimulator.usecase.command

import cats.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.usecase.command.DeleteItemCommandHandler.*

import java.util.UUID

trait DeleteItemCommandHandler[F[_]]:
  def run(command: Command): F[Unit]

object DeleteItemCommandHandler extends DeleteItemCommandHandlerGivens:
  case class Command(id: UUID) derives Hash, Show

trait DeleteItemCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, ItemRepository as itemRepository}] => DeleteItemCommandHandler[F]:
    def run(command: Command): F[Unit] =
      val itemId = ItemId(command.id)
      for {
        itemOpt <- itemRepository.resolveById(itemId)
        _ <- itemOpt.fold(Applicative[F].unit) { item =>
          itemRepository.delete(item)
        }
      } yield ()
  end given
