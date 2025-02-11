package net.raystarkmc.craftingsimulator.port.api.http4s.controller

import cats.*
import cats.instances.all.given
import cats.syntax.all.given
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.syntax.all.given
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler
import org.http4s.*
import org.http4s.circe.*
import org.http4s.implicits.given
import org.http4s.circe.CirceEntityCodec.given
import io.circe.syntax.given
import io.circe.generic.auto.given
import net.raystarkmc.craftingsimulator.usecase.command.RegisterItemCommandHandler.Command

trait RegisterItemController[F[_]]:
  def run(req: Request[F]): F[Response[F]]
    
object RegisterItemController extends RegisterItemControllerGivens

trait RegisterItemControllerGivens:
  given [F[_]: Concurrent: RegisterItemCommandHandler]: RegisterItemController[F] =
    object instance extends RegisterItemController[F]:
      private val dsl = org.http4s.dsl.Http4sDsl[F]
      import dsl.*
      private val handler = summon[RegisterItemCommandHandler[F]]

      def run(req: Request[F]): F[Response[F]] =
        for {
          command <- req.as[Command]
          a <- handler.run(command)
          res <- Ok(a.toString.asJson)
        } yield res
    instance