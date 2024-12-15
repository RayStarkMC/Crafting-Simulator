package net.raystarkmc.craftingsimulator

import cats.effect.IO
import io.circe.generic.auto.given
import io.circe.syntax.given
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.{HttpApp, HttpRoutes, Request, Response}

case class Hello(message: String)

val app: HttpApp[IO] = {
  val routes = HttpRoutes.of[IO] {
    case req@POST -> Root / "hello" => hello(req)
  }
  
  routes.orNotFound
}

def hello(req: Request[IO]) = {
  for {
    body <- req.decodeJson[Hello]
    res <- Ok(body.asJson)
  } yield {
    res
  }
}


