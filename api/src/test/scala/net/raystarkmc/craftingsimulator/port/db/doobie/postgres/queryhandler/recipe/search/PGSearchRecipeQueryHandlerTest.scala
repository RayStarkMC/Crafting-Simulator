package net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.search

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.instances.all.given
import cats.effect.std.*
import cats.effect.unsafe.implicits.global
import cats.instances.all.given
import cats.syntax.all.*
import doobie.ConnectionIO
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.fragments
import doobie.util.transactor.Transactor
import java.time.*
import java.util.UUID
import net.raystarkmc.craftingsimulator.lib.cats.effect.UUIDGenFromSync.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.LocalDBTestEnvironment.given
import net.raystarkmc.craftingsimulator.port.db.doobie.postgres.queryhandler.recipe.search.PGSearchRecipeQueryHandler.given
import net.raystarkmc.craftingsimulator.usecase.query.recipe.SearchRecipesQueryHandler
import net.raystarkmc.craftingsimulator.usecase.query.recipe.SearchRecipesQueryHandler.*
import org.scalatest.freespec.AnyFreeSpec
import scala.collection.immutable.SortedSet

class PGSearchRecipeQueryHandlerTest extends AnyFreeSpec:
  "test" ignore:
    val handler: SearchRecipesQueryHandler[ConnectionIO] = summon

    val runConnectionIO =
      for
        uuids <- UUIDGen
          .randomUUID[ConnectionIO]
          .replicateA:
            30
          .map:
            SortedSet.from
          .map:
            NonEmptySet.fromSet
        input = Input(
          name = none,
          inputIds = uuids,
          outputIds = none,
        )
        output <- handler.run(input)
      yield output

    runConnectionIO.quick.unsafeRunSync()
