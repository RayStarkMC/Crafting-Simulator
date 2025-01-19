package net.raystarkmc.craftingsimulator.port.api.http4s

import cats.effect.IO
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.*
import org.http4s.dsl.io.*
import org.http4s.{HttpRoutes, Request, Response}

val app: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "api" / "items" => registerItem(req)
  case req @ GET -> Root / "api" / "items" => getAllItems(req)
}
