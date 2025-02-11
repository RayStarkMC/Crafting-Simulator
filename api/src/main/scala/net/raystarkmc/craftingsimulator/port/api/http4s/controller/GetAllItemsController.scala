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
import org.http4s.implicits.given
import io.circe.syntax.given
import io.circe.generic.auto.given

trait GetAllItemsController[F[_]]:
  def run(req: Request[F]): F[Response[F]]

object GetAllItemsController extends GetAllItemsControllerGivens

trait GetAllItemsControllerGivens:
  given [F[_]: Concurrent: GetAllItemsQueryHandler]:  GetAllItemsController[F] =
    object instance extends GetAllItemsController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[GetAllItemsQueryHandler[F]]

      def run(req: Request[F]): F[Response[F]] = 
        for {
          queryModel <- handler.run()
          res <- Ok(queryModel.asJson)
        } yield res
    instance
