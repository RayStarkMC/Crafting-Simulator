package net.raystarkmc.craftingsimulator.port.api.http4s

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import org.http4s.{HttpRoutes, Request, Response}
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.GetAllItemsController
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.RegisterItemController

trait Routing[F[_]]:
  val routes: HttpRoutes[F]

object Routing extends RoutingGivens

trait RoutingGivens:
  given [F[_]: Concurrent: GetAllItemsController: RegisterItemController]: Routing[F] =
    object instance extends Routing[F]:
      val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      val routes: HttpRoutes[F] =
        HttpRoutes.of[F] {
          case req @ POST -> Root / "api" / "items" =>
            summon[RegisterItemController[F]].run(req)
          case req @ GET -> Root / "api" / "items" =>
            summon[GetAllItemsController[F]].run(req)
        }
    instance
