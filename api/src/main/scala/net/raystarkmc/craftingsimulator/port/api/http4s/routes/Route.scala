package net.raystarkmc.craftingsimulator.port.api.http4s.routes

import cats.*
import cats.instances.all.given
import cats.syntax.all.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.*
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.recipe.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

def allRoutes[
  F[_]: {Monad, RegisterItemController as RegisterItemController, SearchItemsController as SearchItemsController,
    GetItemController as GetItemController, UpdateItemController as UpdateItemController,
    DeleteItemController as DeleteItemController, RegisterRecipeController as RegisterRecipeController}
]: HttpRoutes[F] =
  HttpRoutes.empty[F]
    <+> RegisterRecipeController.route
    <+> HttpRoutes.of[F](
      PartialFunction.empty
        orElse RegisterItemController.run
        orElse SearchItemsController.run
        orElse GetItemController.run
        orElse UpdateItemController.run
        orElse DeleteItemController.run
    )
