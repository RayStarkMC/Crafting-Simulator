package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import cats.effect.Async
import doobie.util.transactor.Transactor
import doobie.*
import doobie.implicits.given

def xa[F[_]: Async]: Transactor.Aux[F, Unit] = Transactor.fromDriverManager[F](
  driver = "org.postgresql.Driver",
  url = "jdbc:postgresql://db/crafting_simulator",
  user = "admin",
  password = "admin",
  logHandler = None
)