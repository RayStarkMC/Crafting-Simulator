package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import cats.effect.Async
import doobie.*
import doobie.implicits.given
import doobie.util.transactor.Transactor
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.item.PGItemRepositoryInstance

def xa[F[_]: Async]: Transactor[F] = Transactor.fromDriverManager[F](
  driver = "org.postgresql.Driver",
  url = "jdbc:postgresql://db/crafting_simulator",
  user = "admin",
  password = "admin",
  logHandler = None
)

trait TransactionInstances:
  given [F[_]: Async] => Transaction[ConnectionIO, F]:
    def withTransaction[A](program: ConnectionIO[A]): F[A] = program.transact(xa)

trait DbPortInstances extends TransactionInstances with PGItemRepositoryInstance

object instances extends DbPortInstances
