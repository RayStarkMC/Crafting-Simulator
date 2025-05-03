package net.raystarkmc.craftingsimulator.usecase.command.item

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.given
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.command.item.DeleteItemCommandHandler.*

import java.util.UUID

trait DeleteItemCommandHandler[F[_]]:
  def run(command: Command): F[Either[Failure, Unit]]

object DeleteItemCommandHandler extends DeleteItemCommandHandlerGivens:
  case class Command(id: UUID) derives Eq, Hash, Order, Show
  enum Failure derives Eq, Hash, Order, Show:
    case ModelNotFound

trait DeleteItemCommandHandlerGivens:
  given [
      F[_],
      G[_]: {ItemRepository as itemRepository, Monad}
  ] => (T: Transaction[G, F]) => DeleteItemCommandHandler[F]:
    def run(command: Command): F[Either[Failure, Unit]] =
      val itemId = ItemId(command.id)
      val eitherT = T.withTransaction {
        for {
          item <- EitherT.fromOptionF(
            itemRepository.resolveById(itemId),
            Failure.ModelNotFound
          )
          _ <- EitherT.right[Failure] {
            itemRepository.delete(item)
          }
        } yield ()
      }
      eitherT.value
