package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid

object ModTags {
    val inventoryChargeBlacklist: TagKey<Item> = TagKey.create(Registries.ITEM, "inventory_charge_blacklist".tempadId)
    val chrononGens: TagKey<Item> = TagKey.create(Registries.ITEM, "chronon_generators".tempadId)

    val enteringNotSupported: TagKey<Level> = TagKey.create(Registries.DIMENSION, "entering_not_supported".tempadId)
    val leavingNotSupported: TagKey<Level> = TagKey.create(Registries.DIMENSION, "leaving_not_supported".tempadId)
    val intradimensionalTravelNotSupported: TagKey<Level> = TagKey.create(Registries.DIMENSION, "intradimensional_travel_not_supported".tempadId)

    val teleportingNotSupport: TagKey<EntityType<*>> = TagKey.create(Registries.ENTITY_TYPE, "teleporting_not_supported".tempadId)
}