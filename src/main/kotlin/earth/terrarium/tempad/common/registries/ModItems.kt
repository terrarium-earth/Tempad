package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.items.ChrononGenerator
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.items.TemporalBeaconItem
import earth.terrarium.tempad.common.items.TimeTwisterItem
import earth.terrarium.tempad.common.utils.creativeModeTab
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item

object ModItems {
    val registry: ResourcefulRegistry<Item> = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MOD_ID)
    val creativeTabs: ResourcefulRegistry<CreativeModeTab> = ResourcefulRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, Tempad.MOD_ID)

    val tab: CreativeModeTab by creativeTabs.register("main") {
        creativeModeTab {
            title(Component.translatable("category.tempad"))
            icon {
                tempad.defaultInstance
            }
        }
    }

    val tempad: TempadItem by registry.register("tempad") { TempadItem() }
    val chrononGenerator: ChrononGenerator by registry.register("chronon_generator") { ChrononGenerator() }
    val temporalBeacon: Item by registry.register("temporal_beacon") { TemporalBeaconItem() }
    val timeTwister: Item by registry.register("time_twister") { TimeTwisterItem() }

    val locationCard: Item by registry.register("location_card") { Item(Item.Properties()) }
    val newLocationUpgrade: Item by registry.register("new_location_upgrade") { Item(Item.Properties()) }
    val playerTeleportUpgrade: Item by registry.register("player_teleport_upgrade") { Item(Item.Properties()) }
}