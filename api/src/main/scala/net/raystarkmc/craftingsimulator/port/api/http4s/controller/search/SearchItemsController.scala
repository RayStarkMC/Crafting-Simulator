package net.raystarkmc.craftingsimulator.port.api.http4s.controller.search

import cats.*
import cats.effect.*
import cats.syntax.all.given
import io.circe.generic.auto.given
import io.circe.syntax.given
import net.raystarkmc.craftingsimulator.usecase.query.item.SearchItemsQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.item.SearchItemsQueryHandler.Input
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait SearchItemsController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object SearchItemsController:
  case class RequestBody(name: Option[String])

  given [F[_]: {SearchItemsQueryHandler as handler, Http4sDsl as dsl, Concurrent}] => SearchItemsController[F]:
    import dsl.*
    def run: PartialFunction[Request[F], F[Response[F]]] =
      case req @ POST -> Root / "api" / "search" / "items" =>
        for {
          body <- req.as[RequestBody]
          queryModel <- handler.run(Input(name = body.name))
          res <- Ok(queryModel.asJson)
        } yield res
