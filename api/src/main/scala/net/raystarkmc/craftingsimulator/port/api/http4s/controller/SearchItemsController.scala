package net.raystarkmc.craftingsimulator.port.api.http4s.controller

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import cats.effect.Concurrent
import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler.Input
import org.http4s.*
import org.http4s.circe.*
import org.http4s.implicits.given
import io.circe.syntax.given
import io.circe.generic.auto.given

trait SearchItemsController[F[_]]:
  def run(req: Request[F]): F[Response[F]]

object SearchItemsController extends SearchItemsControllerGivens

trait SearchItemsControllerGivens:
  given [F[_]: {Concurrent, SearchItemsQueryHandler}] => SearchItemsController[F] =
    object instance extends SearchItemsController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[SearchItemsQueryHandler[F]]

      def run(req: Request[F]): F[Response[F]] =
        val name = req.params.get("name")
        for {
          queryModel <- handler.run(Input(name = name))
          res <- Ok(queryModel.asJson)
        } yield res
    instance