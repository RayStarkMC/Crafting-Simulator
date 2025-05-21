package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.effect.Concurrent
import cats.instances.all.given
import cats.syntax.all.*
import io.circe.generic.auto.given
import io.circe.syntax.given
import java.util.UUID
import net.raystarkmc.craftingsimulator.port.api.http4s.controller.item.GetItemController.ResponseBody
import net.raystarkmc.craftingsimulator.usecase.query.item.GetItemQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.item.GetItemQueryHandler.Input
import org.http4s.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait GetItemController[F[_]]:
  def route: HttpRoutes[F]

object GetItemController:
  case class ResponseBody(
    id: UUID,
    name: String,
  )

  given [F[_]: {Concurrent, GetItemQueryHandler as handler, Http4sDsl as dsl}] => GetItemController[F]:
    import dsl.*

    def route: HttpRoutes[F] =
      HttpRoutes.of:
        case req @ GET -> Root / "api" / "items" / UUIDVar(itemId) =>
          val input = Input(id = itemId)

          val optionT =
            for
              item <- OptionT:
                handler.run(input)
              responseBody = ResponseBody(
                id = item.id,
                name = item.name,
              )
              response <- OptionT.liftF:
                Ok(responseBody.asJson)
            yield response

          optionT.getOrElseF:
            NotFound()
