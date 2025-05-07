package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import cats.effect.Async
import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor

val testTransactor = Transactor.after
  .set(
    Transactor
      .fromDriverManager[IO](
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5432/crafting_simulator",
        user = "admin",
        password = "admin",
        logHandler = None,
      ),
    FC.rollback,
  )
  .yolo
