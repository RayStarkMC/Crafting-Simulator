package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import net.raystarkmc.craftingsimulator.domain.item.{
  Item,
  ItemId,
  ItemName,
  ItemRepository
}
import cats.syntax.all.given
import cats.instances.all.given
import cats.data.*
import cats.*
import cats.effect.{Async, IO}
import cats.free.Free
import doobie.*
import doobie.implicits.given

import java.util.UUID

trait PGItemRepository extends ItemRepository[IO]:
  //FIXME: メソッド内トランザクション解除
  override def resolveById(itemId: ItemId): IO[Option[Item]] =
    val query =
      sql"select item.id, item.name from item where id = ${itemId.value.toString}"
        .query[(String, String)]
        .option

    val optionT = for {
      (id, name) <- OptionT(query.transact[IO](xa))
      itemId = ItemId(UUID.fromString(id).nn)
      itemName: ItemName <-
        ItemName.either(name) match {
          case Left(err) =>
            OptionT.liftF(
              ApplicativeError[IO, Throwable].raiseError[ItemName](
                new RuntimeException(err.show)
              )
            )
          case Right(v) =>
            OptionT[IO, ItemName](
              IO.pure(Option(v))
            )
        }
    } yield {
      Item.restore(
        data = Item.Data(
          id = itemId,
          name = itemName
        )
      )
    }
    optionT.value

  override def save(item: Item): IO[Unit] =
    val insertSql =
      sql"insert into item (id, name) values (${item.data.id.value.toString}::uuid, ${item.data.name.value})".update.run

    insertSql.void.transact[IO](xa)

object PGItemRepository extends PGItemRepositoryGivens

trait PGItemRepositoryGivens:
  given ItemRepository[IO] =
    object repository extends PGItemRepository
    repository
