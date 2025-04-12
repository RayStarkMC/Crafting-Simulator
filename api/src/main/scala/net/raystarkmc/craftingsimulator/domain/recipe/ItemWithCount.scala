package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.implicits.*
import cats.derived.*
import io.github.iltotore.iron.cats.{*, given}
import net.raystarkmc.craftingsimulator.domain.item.ItemId
import net.raystarkmc.craftingsimulator.domain.item.ItemId.{*, given}

case class ItemWithCount(item: ItemId, count: ItemCount) derives Hash, Show