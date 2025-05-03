package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.data.*
import cats.effect.*
import cats.syntax.all.given
import io.circe.generic.auto.given
import io.circe.syntax.given
import net.raystarkmc.craftingsimulator.usecase.command.item.RegisterItemCommandHandler
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.given
import org.http4s.dsl.*

trait RegisterItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object RegisterItemController:
  given [F[_]: {RegisterItemCommandHandler as handler, Http4sDsl as dsl, Concurrent}] => RegisterItemController[F]:

    import dsl.*

    def run: PartialFunction[Request[F], F[Response[F]]] =
      case req @ POST -> Root / "api" / "items" =>
        val eitherT = for {
          command <- EitherT.right[RegisterItemCommandHandler.Failure] {
            req.as[RegisterItemCommandHandler.Command]
          }
          RegisterItemCommandHandler.Output(id) <- EitherT(handler.run(command))
          res <- EitherT.right[RegisterItemCommandHandler.Failure] {
            Ok(id.show)
          }
        } yield res
        eitherT.leftMap(_.show).valueOrF(BadRequest(_))
