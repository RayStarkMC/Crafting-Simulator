package net.raystarkmc.craftingsimulator.domain.recipe

import cats.*
import cats.derived.*
import net.raystarkmc.craftingsimulator.domain.item.*

case class ItemWithCount(item: ItemId, count: ItemCount) derives Eq, Hash, Order, Show