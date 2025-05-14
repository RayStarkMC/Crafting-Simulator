package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import cats.*
import cats.data.*
import cats.effect.Async
import cats.effect.IO
import cats.instances.all.given
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import doobie.util.yolo.Yolo
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction

trait LocalDBTestEnvironment extends Transaction.Noop:
  val testYolo: Yolo[IO] = Transactor.after
    .set(
      Transactor.fromDriverManager[IO](
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5432/crafting_simulator",
        user = "admin",
        password = "admin",
        logHandler = LogHandler.jdkLogHandler[IO].some,
      ),
      FC.rollback,
    )
    .yolo

  export testYolo.*

object LocalDBTestEnvironment extends LocalDBTestEnvironment
