package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe

import cats.*
import cats.data.*
import cats.effect.instances.all.given
import cats.effect.unsafe.implicits.global
import cats.instances.all.given
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import net.raystarkmc.craftingsimulator.domain.item.*
import net.raystarkmc.craftingsimulator.domain.recipe.*
import net.raystarkmc.craftingsimulator.lib.cats.*
import net.raystarkmc.craftingsimulator.lib.cats.effect.UUIDGenFromSync.given
import net.raystarkmc.craftingsimulator.lib.domain.*
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction
import net.raystarkmc.craftingsimulator.lib.transaction.Transaction.Noop.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.LocalDBTestEnvironment.*
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.repository.recipe.PGRecipeRepository.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.xa
import org.scalatest.freespec.AnyFreeSpec

class PGRecipeRepositoryTest extends AnyFreeSpec:
  "sample" ignore:
    val repository: RecipeRepository[ConnectionIO] = summon

    val program = for {
      name <- ApplicativeThrow[ConnectionIO].fromEither:
        ModelName
          .inParallel[EitherNec[ModelName.Failure, _]]:
            "name"
          .leftMap: _ =>
            fail()
          .map:
            RecipeName.apply
      itemCount <- ApplicativeThrow[ConnectionIO].fromEither:
        ItemCount
          .ae[EitherNec[ItemCount.Failure, _]]:
            10L
          .leftMap: _ =>
            fail()
      itemId <- ItemId.generate[ConnectionIO]
      recipe <- Recipe.create[ConnectionIO](
        name = name,
        inputs = RecipeInput:
          Seq(
            ItemWithCount(
              item = itemId,
              count = itemCount,
            )
          )
        ,
        outputs = RecipeOutput:
          Seq(
            ItemWithCount(
              item = itemId,
              count = itemCount,
            )
          ),
      )
      _ <- repository.save(recipe)
      model <- repository.resolveById(recipe.id)
    } yield (model, recipe)

    program.quick.unsafeRunSync()
