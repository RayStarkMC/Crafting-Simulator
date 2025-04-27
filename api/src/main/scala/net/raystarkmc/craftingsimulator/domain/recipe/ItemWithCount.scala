package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.derived.*
import cats.implicits.*
import net.raystarkmc.craftingsimulator.domain.item.ItemId
import net.raystarkmc.craftingsimulator.domain.item.ItemId.{*, given}

case class ItemWithCount(item: ItemId, count: ItemCount) derives Hash, Show