package net.raystarkmc.craftingsimulator.lib.domain

import cats.*
import cats.implicits.*
import cats.derived.*
import cats.data.*

opaque type ModelName = String
object ModelName extends ModelNameGivens:
  extension(self: ModelName)
    def value: String = self

  def unapply(s: ModelName): String = s

  enum Failure derives Hash, Show:
    case IsBlank extends Failure
    case LengthExceeded extends Failure
    case ContainsControlCharacter extends Failure

  def ae[F[_]: ([G[_]] =>> ApplicativeError[G, NonEmptyChain[Failure]]) as F](
      value: String
  ): F[ModelName] =

    val pattern = ".*\\p{Cntrl}.*".r
    
    F.raiseWhen(value.isBlank)(NonEmptyChain.one(Failure.IsBlank)) *> 
      F.raiseWhen(value.length > 100)(NonEmptyChain.one(Failure.LengthExceeded)) *>
      F.raiseWhen(pattern.findFirstIn(value).nonEmpty)(NonEmptyChain.one(Failure.ContainsControlCharacter)) *>
      F.pure(value)

private inline def wrapF[F[_]](f: F[String]): F[ModelName] = f

private trait ModelNameGivens:
  given Eq[ModelName] = wrapF(Eq[String])
  given Hash[ModelName] = wrapF(Hash[String])
  given Order[ModelName] = wrapF(Order[String])
  given Show[ModelName] = wrapF(Show[String])