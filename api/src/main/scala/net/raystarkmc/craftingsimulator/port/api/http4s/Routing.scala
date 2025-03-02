package net.raystarkmc.craftingsimulator.port.api.http4s

import cats.*
import cats.effect.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.{DeleteItemController, GetItemController, RegisterItemController, SearchItemsController, UpdateItemController}
import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler
import org.http4s.{HttpRoutes, Request, Response}

trait Routing[F[_]]:
  val routes: HttpRoutes[F]

object Routing extends RoutingGivens

trait RoutingGivens:
  given [
      F[_]: {Concurrent, RegisterItemController, SearchItemsQueryHandler,
        DeleteItemController, UpdateItemController, GetItemController}
  ]
    => Routing[F] =
    object instance extends Routing[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      val routes: HttpRoutes[F] =
        HttpRoutes.of[F](
          summon[DeleteItemController[F]].run
            .orElse(summon[UpdateItemController[F]].run)
            .orElse(summon[GetItemController[F]].run)
            .orElse {
              case req @ POST -> Root / "api" / "items" =>
                summon[RegisterItemController[F]].run(req)
              case req @ POST -> Root / "api" / "search" / "items" =>
                summon[SearchItemsController[F]].run(req)
            }
        )
    instance
