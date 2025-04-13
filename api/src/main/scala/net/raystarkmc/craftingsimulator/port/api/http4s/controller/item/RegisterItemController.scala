package net.raystarkmc.craftingsimulator.port.api.http4s.controller.item

import cats.*
import cats.effect.*
import cats.syntax.all.given
import io.circe.generic.auto.given
import io.circe.syntax.given
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler.Command
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.given

trait RegisterItemController[F[_]]:
  def run: PartialFunction[Request[F], F[Response[F]]]

object RegisterItemController extends RegisterItemControllerGivens

trait RegisterItemControllerGivens:
  given [F[_]: {Concurrent, RegisterItemCommandHandler}]
    => RegisterItemController[F] =
    object instance extends RegisterItemController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[RegisterItemCommandHandler[F]]

      def run: PartialFunction[Request[F], F[Response[F]]] =
        case req @ POST -> Root / "api" / "items" =>
          for {
            command <- req.as[Command]
            a <- handler.run(command)
            res <- Ok(a.toString.asJson)
          } yield res
    instance
