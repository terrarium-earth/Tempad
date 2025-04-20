package earth.terrarium.tempad.data

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.data.client.ModBlockStateData
import earth.terrarium.tempad.data.client.ModItemModelData
import earth.terrarium.tempad.data.client.ModLang
import earth.terrarium.tempad.data.server.ModBlockTags
import earth.terrarium.tempad.data.server.ModEntityTags
import earth.terrarium.tempad.data.server.ModItemTags
import earth.terrarium.tempad.data.server.ModLootTables
import earth.terrarium.tempad.data.server.ModRecipeData
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent

@Mod(Tempad.MOD_ID)
class TempadData(bus: IEventBus) {
    init {
        bus.addListener(::gatherData)
    }

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        generator.addProvider(event.includeClient(), ModBlockStateData(output, existingFileHelper))
        generator.addProvider(event.includeClient(), ModItemModelData(output, existingFileHelper))
        generator.addProvider(event.includeClient(), ModLang(output))
        generator.addProvider(event.includeServer(), ModRecipeData(output, lookupProvider))
        generator.addProvider(event.includeServer(), ModLootTables(output, lookupProvider))
        generator.addProvider(event.includeServer(), ModEntityTags(output, lookupProvider, existingFileHelper))
        val blockTags = ModBlockTags(output, lookupProvider, existingFileHelper)
        generator.addProvider(event.includeServer(), blockTags)
        generator.addProvider(event.includeServer(), ModItemTags(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper))

    }
}