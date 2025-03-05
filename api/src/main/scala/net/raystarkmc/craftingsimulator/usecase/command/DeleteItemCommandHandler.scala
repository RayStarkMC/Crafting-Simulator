package net.raystarkmc.craftingsimulator.usecase.command

import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import cats.{Applicative, Hash, Monad, Show}
import net.raystarkmc.craftingsimulator.domain.item.{ItemId, ItemRepository}
import net.raystarkmc.craftingsimulator.usecase.command.DeleteItemCommandHandler.{
  Command,
  Output
}
import io.github.iltotore.iron.*

import java.util.UUID

trait DeleteItemCommandHandler[F[_]]:
  def run(command: Command): F[Output]

object DeleteItemCommandHandler extends DeleteItemCommandHandlerGivens:
  case class Command(id: UUID) derives Hash, Show
  case class Output() derives Hash, Show

trait DeleteItemCommandHandlerGivens:
  given [F[_]: {Monad, UUIDGen, ItemRepository}]
    => DeleteItemCommandHandler[F] =
    object instance extends DeleteItemCommandHandler[F]:
      private val itemRepository = summon[ItemRepository[F]]

      def run(
          command: Command
      ): F[Output] =
        val itemId = ItemId(command.id)
        for {
          itemOpt <- itemRepository.resolveById(itemId)
          _ <- itemOpt.fold(Applicative[F].unit) { item =>
            itemRepository.delete(item)
          }
        } yield Output()
    instance
