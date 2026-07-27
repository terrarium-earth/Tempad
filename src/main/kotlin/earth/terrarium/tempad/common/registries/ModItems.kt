package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullib.common.registry.builtin.ResourcefulItemRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.items.*
import earth.terrarium.tempad.common.utils.creativeModeTab
import earth.terrarium.tempad.common.utils.stack
import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item

object ModItems {
    val registry = ResourcefulRegistries.createForItems(Tempad.MOD_ID)
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
    val locationCard: Item by registry.registerItem("location_card", ::LocationCardItem) { it.stacksTo(16) }
    val cardWallet: Item by registry.registerItem("card_wallet", ::WalletItem) { it.stacksTo(1) }
    val timeSteel: Item by registry.registerSimple("time_steel")
    val knowledgeProjector: Item by registry.registerItem("knowledge_projector", ::HandbookItem)

    val newLocationUpgrade: Item by registry.registerSimple("new_location_upgrade")
    val newLocationKey = "new_location".tempadId

    val playerTeleportUpgrade: Item by registry.registerSimple("player_teleport_upgrade")
    val playerKey = "player_teleport".tempadId

    val guideUpgrade: Item by registry.registerSimple("knowledge_repository_upgrade")
    val guideKey = "knowledge_repository".tempadId

    val temputerIron: Item by registry.registerItem("temputer_iron", ::TemputerIron) { it.stacksTo(1) }
    val temputerTimeSteel: Item by registry.registerItem("temputer_time_steel", { BlockItem(ModBlocks.temputerTimeSteel, it) }) { it.stacksTo(1) }

    val doorpointIron: Item by registry.registerItem("doorpoint_iron", { DoorpointItem(ModBlocks.doorpointIron, it) })
    val doorpointTimeSteel: Item by registry.registerItem("doorpoint_time_steel", { DoorpointItem(ModBlocks.doorpointTimeSteel, it) })

    val capacitorIron: Item by registry.registerItem("chronon_capacitor_iron", ::ChrononCapacitorItem) { it.stacksTo(1) }
    val capacitorTimeSteel: Item by registry.registerItem("chronon_capacitor_time_steel", ::ChrononCapacitorItem) { it.stacksTo(1) }

    val chrononGenIron: ChrononGeneratorItem by registry.registerItem("chronon_generator_iron", { ChrononGeneratorItem(it, CommonConfig.ChrononGenerator::generationRate, CommonConfig.ChrononGenerator::generationAmount) } ) {
        it.stacksTo(1)
    }
    val chrononGenTimeSteel: ChrononGeneratorItem by registry.registerItem("chronon_generator_time_steel", { ChrononGeneratorItem(it, CommonConfig.Chronometer::generationRate, CommonConfig.Chronometer::generationAmount) } ) { it.stacksTo(1) }

    val waymitterIron: Item by registry.registerItem("waymitter_iron", ::WaymitterIron) { it.stacksTo(1) }
    val waymitterTimeSteel: Item by registry.registerItem("waymitter_time_steel", ::WaymitterTimeSteel) { it.stacksTo(1) }

    val tempad: TempadItem by registry.registerItem("tempad", ::TempadItem) {
        it.stacksTo(1)
        it.component(ModComponents.serialPrefix, "TERRA")
    }

    val timeTwister: Item by registry.registerItem("time_twister", ::TimeTwisterItem) { it.stacksTo(1) }
    val workstation: Item by registry.registerItem("workstation", ::WorkstationItem) { it.stacksTo(1) }
    val metronome: Item by registry.registerItem("metronome", ::MetronomeItem) { it.stacksTo(1) }
    val liftway: Item by registry.registerItem("liftway", { BlockItem(ModBlocks.liftway, it) }) { it.stacksTo(1) }

    // Creative
    val creativeChronometer: Item by registry.registerItem("creative_chronometer", ::CreativeChronometerItem) { it.stacksTo(1) }
}

fun <T: Item> ResourcefulItemRegistry.registerItem(id: String, item: (Item.Properties) -> T, props: (Item.Properties) -> Item.Properties = { it }) = register(id, item) { props(Item.Properties()) }

fun ResourcefulItemRegistry.registerSimple(id: String, props: (Item.Properties) -> Item.Properties = { it }) = register(id, ::Item) { props(Item.Properties()) }