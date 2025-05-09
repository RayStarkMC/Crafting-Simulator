package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import cats.*
import cats.data.*
import cats.effect.Async
import cats.instances.all.given
import cats.syntax.all.*
import doobie.*
import doobie.implicits.given
import doobie.util.transactor.Transactor
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGGetItemQueryHandler
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGSearchItemsQueryHandler
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.get.PGGetRecipeQueryHandler
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.item.PGItemRepository
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe.PGRecipeRepository

def xa[F[_]: Async]: Transactor[F] = Transactor.fromDriverManager[F](
  driver = "org.postgresql.Driver",
  url = "jdbc:postgresql://db/crafting_simulator",
  user = "admin",
  password = "admin",
  logHandler = None,
)

trait DoobieTransaction:
  given [F[_]: Async] => Transaction[ConnectionIO, F]:
    def withTransaction[A](program: ConnectionIO[A]): F[A] =
      program.transact(xa)

    def withTransaction[E, A](program: EitherT[ConnectionIO, E, A]): EitherT[F, E, A] =
      program.transact(xa)

trait DbPortInstances
  extends DoobieTransaction,
    PGItemRepository,
    PGRecipeRepository,
    PGGetItemQueryHandler,
    PGSearchItemsQueryHandler,
    PGGetRecipeQueryHandler

object instances extends DbPortInstances
