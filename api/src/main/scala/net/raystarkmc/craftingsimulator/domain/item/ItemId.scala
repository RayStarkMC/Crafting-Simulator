package net.raystarkmc.craftingsimulator.domain.item

import net.raystarkmc.craftingsimulator.lib.domain.ModelIdUUID

type ItemId = ItemId.T
object ItemId extends ModelIdUUID[ItemContext]