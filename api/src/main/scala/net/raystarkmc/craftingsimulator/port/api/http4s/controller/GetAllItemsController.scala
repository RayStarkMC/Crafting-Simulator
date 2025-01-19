package net.raystarkmc.craftingsimulator.port.api.http4s.controller

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import net.raystarkmc.craftingsimulator.usecase.query.GetAllItemsQueryHandler
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.PGGetAllItemsQueryHandler.given
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.implicits.given
import io.circe.syntax.given
import io.circe.generic.auto.given

def getAllItems(req: Request[IO]): IO[Response[IO]] =
  val handler = summon[GetAllItemsQueryHandler[IO]]

  for {
    queryModel <- handler.run()
    res <- Ok(queryModel.asJson)
  } yield res