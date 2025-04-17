package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.items.*
import earth.terrarium.tempad.common.utils.creativeModeTab
import earth.terrarium.tempad.common.utils.stack
import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item

object ModItems {
    val registry: ResourcefulRegistry<Item> = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MOD_ID)
    val creativeTabs: ResourcefulRegistry<CreativeModeTab> = ResourcefulRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, Tempad.MOD_ID)

    val tab: CreativeModeTab by creativeTabs.register("main") {
        creativeModeTab {
            title(Component.translatable("category.tempad"))
            icon {
                tempad.stack {
                    twisterEquipped = true
                }
            }
        }
    }

    // Base
    val locationCard: Item by registry.register("location_card") { LocationCardItem() }
    val cardWallet: Item by registry.register("card_wallet") { WalletItem() }
    val timeSteel: Item by registry.register("time_steel") { Item(Item.Properties()) }
    val handbook: Item by registry.register("knowledge_projector") { HandbookItem() }

    val newLocationUpgrade: Item by registry.register("new_location_upgrade") { Item(Item.Properties().requiredFeatures(Tempad.flag)) }
    val newLocationKey = "new_location".tempadId

    val playerTeleportUpgrade: Item by registry.register("player_teleport_upgrade") { Item(Item.Properties()) }
    val playerKey = "player_teleport".tempadId

    // Rudi Tier
    val timedoorProjector: Item by registry.register("timedoor_projector") { RudimentaryTempadItem() }
    val timedoorMarker: Item by registry.register("timedoor_marker") { SpatialAnchorItem(ModBlocks.timedoorMarker) }
    val chrononCell: CapacitorItem by registry.register("chronon_cell") { CapacitorItem() }
    val chrononGenerator: ChronometerItem by registry.register("chronon_generator") { ChronometerItem(CommonConfig.ChrononGenerator::generationRate, CommonConfig.ChrononGenerator::generationAmount) }
    val locationBroadcaster: Item by registry.register("location_broadcaster") { LocationBroadcasterItem() }

    // TVA Tier
    val tempad: TempadItem by registry.register("tempad") { TempadItem() }
    val chronomark: Item by registry.register("chronomark") { SpatialAnchorItem(ModBlocks.chronomark) }
    val chrononBattery: Item by registry.register("chronon_battery") { CapacitorItem() }
    val chronometer: ChronometerItem by registry.register("chronometer") { ChronometerItem(CommonConfig.Chronometer::generationRate, CommonConfig.Chronometer::generationAmount) }
    val timeTwister: Item by registry.register("time_twister") { TimeTwisterItem() }
    val workstation: Item by registry.register("workstation") { WorkstationItem() }
    val metronome: Item by registry.register("metronome") { MetronomeItem() }
    val screeningDevice: Item by registry.register("screening_device") { ScreeningDeviceItem() }

    // Creative
    val creativeChronometer: Item by registry.register("creative_chronometer") { CreativeChronometerItem() }
}