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
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent

@Mod(Tempad.MOD_ID)
@EventBusSubscriber(modid = Tempad.MOD_ID)
object TempadData {

    @SubscribeEvent
    fun gatherClientData(event: GatherDataEvent.Client) {
        val generator = event.generator
        val output = generator.packOutput
        print("CLIENT DATA RUNNING")

        generator.addProvider(true, ModBlockStateData(output))
        generator.addProvider(true, ModItemModelData(output))
        generator.addProvider(true, ModLang(output))
    }

    @SubscribeEvent
    fun gatherServerData(event: GatherDataEvent.Server) {
        val generator = event.generator
        val output = generator.packOutput
        val lookupProvider = event.lookupProvider
        print("SERVER DATA RUNNING")

        generator.addProvider(true, ModRecipeData(output, lookupProvider))
        generator.addProvider(true, ModLootTables(output, lookupProvider))
        generator.addProvider(true, ModEntityTags(output, lookupProvider))
        generator.addProvider(true, ModBlockTags(output, lookupProvider))
        generator.addProvider(true, ModItemTags(output, lookupProvider))
    }
}
