package net.raystarkmc.craftingsimulator.domain.item

import cats.*
import cats.data.*
import cats.implicits.*
import net.raystarkmc.craftingsimulator.lib.domain.ModelName

opaque type ItemName = ModelName
object ItemName extends ItemNameGivens:
  extension (self: ItemName)
    def value: String = unwrap(self)
  
  type Failure = ModelName.Failure
  val Failure: ModelName.Failure.type = ModelName.Failure

  def ae[F[_]](value: String)(using ApplicativeError[F, NonEmptyChain[Failure]]): F[ItemName] =
    ModelName.ae(value)

private inline def unwrap(modelName: ModelName): String = modelName.value
private inline def wrapF[F[_]](f: F[ModelName]): F[ItemName] = f

trait ItemNameGivens:
  given Eq[ItemName] = wrapF(Eq[ModelName])
  given Hash[ItemName] = wrapF(Hash[ModelName])
  given Order[ItemName] = wrapF(Order[ModelName])
  given Show[ItemName] = wrapF(Show[ModelName])