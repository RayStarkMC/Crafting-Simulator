package net.raystarkmc.craftingsimulator.port.api.http4s.routes

import cats.*
import cats.effect.*
import cats.effect.std.*
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.recipe.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.search.*
import net.raystarkmc.craftingsimulator.port.api.http4s.routes.allRoutes
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.instances.given
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

given [F[_]] => Http4sDsl[F] = Http4sDsl[F]

def allRoutes[F[_]: {Async, UUIDGen}]: HttpRoutes[F] =
  HttpRoutes.empty[F]
    <+> summon[RegisterRecipeController[F]].route
    <+> summon[SearchRecipesController[F]].route
    <+> summon[UpdateRecipeController[F]].route
    <+> summon[GetRecipeController[F]].route
    <+> summon[DeleteRecipeController[F]].route
    <+> HttpRoutes.of[F](
      PartialFunction.empty
        orElse summon[RegisterItemController[F]].run
        orElse summon[SearchItemsController[F]].run
        orElse summon[GetItemController[F]].run
        orElse summon[UpdateItemController[F]].run
        orElse summon[DeleteItemController[F]].run
    )
