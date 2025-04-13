package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.effect.*
import cats.syntax.all.given
import io.circe.generic.auto.given
import io.circe.syntax.given
import SearchItemsController.RequestBody
import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.SearchItemsQueryHandler.Input
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.given

trait SearchItemsController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object SearchItemsController extends SearchItemsControllerGivens {
  case class RequestBody(
      name: Option[String]
  )
}

trait SearchItemsControllerGivens:
  given [F[_]: {Concurrent, SearchItemsQueryHandler}]
    => SearchItemsController[F] =
    object instance extends SearchItemsController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[SearchItemsQueryHandler[F]]

      def run: PartialFunction[Request[F], F[Response[F]]] =
        case req @ POST -> Root / "api" / "search" / "items" =>
          for {
            body <- req.as[RequestBody]
            queryModel <- handler.run(Input(name = body.name))
            res <- Ok(queryModel.asJson)
          } yield res
    instance
