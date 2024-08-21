package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid

object ModTags {
    val enteringNotSupported: TagKey<Level> = TagKey.create(Registries.DIMENSION, "entering_not_supported".tempadId)
    val leavingNotSupported: TagKey<Level> = TagKey.create(Registries.DIMENSION, "leaving_not_supported".tempadId)

    val chrononAcceptor: TagKey<Item> = TagKey.create(Registries.ITEM, "chronon_acceptor".tempadId)
}