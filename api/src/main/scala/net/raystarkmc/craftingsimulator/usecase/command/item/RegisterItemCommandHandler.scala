package net.raystarkmc.craftingsimulator.usecase.command.item

import cats.*
import cats.data.*
import cats.derived.*
import cats.effect.std.UUIDGen
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.lib.cats.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.usecase.command.item.RegisterItemCommandHandler.*

import java.util.UUID

trait RegisterItemCommandHandler[F[_]]:
  def run(command: Command): F[Either[Failure, Output]]

object RegisterItemCommandHandler extends RegisterItemCommandHandlerGivens:
  case class Command(name: String) derives Eq, Hash, Show, Order
  case class Output(id: UUID) derives Eq, Hash, Show, Order
  enum Failure derives Eq, Hash, Show, Order:
    case ValidationFailed(detail: String) extends Failure

trait RegisterItemCommandHandlerGivens:
  given [
      F[_]: {UUIDGen, Monad},
      G[_]: {ItemRepository as itemRepository, Functor}
  ] => (T: Transaction[G, F]) => RegisterItemCommandHandler[F]:
    def run(
        command: Command
    ): F[Either[Failure, Output]] =
      val eitherT = for {
        name <- ModelName
          .inParallel[EitherWithNec[ModelName.Failure]](command.name)
          .map(ItemName.apply)
          .leftMap { e => Failure.ValidationFailed(e.show) }
          .toEitherT[F]
        item <- EitherT.right[Failure](
          Item.create[F](name)
        )
        _ <- T.withTransaction {
          EitherT.right[Failure](itemRepository.save(item))
        }
      } yield Output(item.id.value)

      eitherT.value
