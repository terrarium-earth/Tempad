package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType

object ModTags {
    val chargeBlacklist: TagKey<Item> = TagKey.create(Registries.ITEM, "auto_charge_blacklist".tempadId)

    val chrononGens: TagKey<Item> = TagKey.create(Registries.ITEM, "chronon_generators".tempadId)
    val batteries: TagKey<Item> = TagKey.create(Registries.ITEM, "batteries".tempadId)

    val enteringNotSupported: TagKey<DimensionType> = TagKey.create(Registries.DIMENSION_TYPE, "entering_not_supported".tempadId)
    val leavingNotSupported: TagKey<DimensionType> = TagKey.create(Registries.DIMENSION_TYPE, "leaving_not_supported".tempadId)
    val intradimensionalTravelNotSupported: TagKey<DimensionType> = TagKey.create(Registries.DIMENSION_TYPE, "intradimensional_travel_not_supported".tempadId)

    val teleportingNotSupport: TagKey<EntityType<*>> = TagKey.create(Registries.ENTITY_TYPE, "teleporting_not_supported".tempadId)
}