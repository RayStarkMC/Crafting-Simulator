package net.raystarkmc.craftingsimulator.port.api.http4s.controller

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.PGItemRepository.given
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.implicits.given
import org.http4s.circe.CirceEntityCodec.given
import io.circe.syntax.given
import io.circe.generic.auto.given
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler.Command

def registerItem(req: Request[IO]): IO[Response[IO]] =
  val handler = summon[RegisterItemCommandHandler[IO]]

  for {
    command <- req.as[Command]
    a <- handler.run(command)
    res <- Ok(a.toString.asJson)
  } yield res
