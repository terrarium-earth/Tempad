package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid

object ModTags {
    val enteringNotSupported: TagKey<Level> = TagKey.create(Registries.DIMENSION, "entering_not_supported".tempadId)
    val leavingNotSupported: TagKey<Level> = TagKey.create(Registries.DIMENSION, "leaving_not_supported".tempadId)

    val ITEM_FUEL: TagKey<Item> = TagKey.create(Registries.ITEM, "fuel".tempadId)
    val LIQUID_FUEL: TagKey<Fluid> = TagKey.create(Registries.FLUID, "fuel".tempadId)
}