package net.raystarkmc.craftingsimulator.port.api.http4s.controller

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.Concurrent
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.GetItemController.ResponseBody
import net.raystarkmc.craftingsimulator.usecase.command.DeleteItemCommandHandler
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler.Input
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.given
import io.circe.syntax.given
import io.circe.generic.auto.given

import java.util.UUID

trait GetItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object GetItemController extends GetItemControllerGivens {
  case class ResponseBody(
      id: UUID,
    name: String,
  )
}

trait GetItemControllerGivens:
  given [F[_]: {Concurrent, GetItemQueryHandler}]
  => GetItemController[F] =
    object instance extends GetItemController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[GetItemQueryHandler[F]]

      def run: PartialFunction[Request[F], F[Response[F]]] =
        case req@GET -> Root / "api" / "items" / UUIDVar(itemId) =>
          val input = Input(
            id = itemId
          )
          
          for {
            itemOption <- handler.run(input)
            res <- itemOption.fold(NotFound()) { item =>
              Ok(ResponseBody(
                id = item.id,
                name = item.name
              ).asJson)
            }
          } yield res
    instance