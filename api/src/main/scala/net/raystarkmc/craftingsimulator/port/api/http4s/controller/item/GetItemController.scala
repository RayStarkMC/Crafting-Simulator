package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.*
import cats.effect.Concurrent
import io.circe.generic.auto.given
import io.circe.syntax.given
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.GetItemController.ResponseBody
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.GetItemQueryHandler.Input
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

import java.util.UUID

trait GetItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object GetItemController extends GetItemControllerGivens {
  case class ResponseBody(
      id: UUID,
      name: String
  )
}

trait GetItemControllerGivens:
  given [F[_]: {Concurrent, GetItemQueryHandler as handler, Http4sDsl as dsl}] => GetItemController[F]:
    import dsl.*

    def run: PartialFunction[Request[F], F[Response[F]]] = {
      case req @ GET -> Root / "api" / "items" / UUIDVar(itemId) =>
        val input = Input(id = itemId)

        val optionT = for {
          item <- OptionT(handler.run(input))
          responseBody = ResponseBody(
            id = item.id,
            name = item.name
          ).asJson
          response <- OptionT.liftF(Ok(responseBody))
        } yield response

        optionT.getOrElseF(NotFound())
    }
