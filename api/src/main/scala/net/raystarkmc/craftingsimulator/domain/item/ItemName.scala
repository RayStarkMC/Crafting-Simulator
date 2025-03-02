package net.raystarkmc.craftingsimulator.domain.item

import net.raystarkmc.craftingsimulator.lib.domain.{ModelName, ModelNameSyntax}

type ItemName = ModelName[ItemContext]

object ItemName extends ModelNameSyntax[ItemContext]