package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.items.ChrononGenerator
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.items.TemporalBeaconItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

object ModItems {
    val registry: ResourcefulRegistry<Item> = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MOD_ID)

    val tempad: TempadItem by registry.register("tempad") { TempadItem() }
    val chrononGenerator: ChrononGenerator by registry.register("chronon_generator") { ChrononGenerator() }
    val temporalBeacon: Item by registry.register("temporal_beacon") { TemporalBeaconItem() }

    val blankCard: Item by registry.register("blank_card") { Item(Item.Properties()) }
    val locationCard: Item by registry.register("location_card") { Item(Item.Properties()) }
}