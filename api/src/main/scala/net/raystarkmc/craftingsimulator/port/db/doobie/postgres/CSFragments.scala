package net.raystarkmc.craftingsimulator.port.db.doobie.postgres

import cats.*
import cats.data.*
import cats.instances.all.given
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.fragment.*
import doobie.util.fragments.*

object CSFragments:
  /** Returns `VALUES (fs0, current_timestamp, current_timestamp), (fs1, current_timestamp, current_timestamp), ...`. */
  def valuesFollowedBy2CurrentTimestamps[F[_]: Reducible, A](fs: F[A])(implicit w: util.Write[A]): Fragment =
    fr"VALUES" ++ comma:
      fs.toNonEmptyList.map: f =>
        parentheses:
          comma(values(f), current_timestamp, current_timestamp)

  def current_timestamp: Fragment = fr"current_timestamp"
