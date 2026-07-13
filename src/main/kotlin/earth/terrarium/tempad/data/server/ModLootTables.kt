package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture

class ModLootTables(packOutput: PackOutput, provider: CompletableFuture<HolderLookup.Provider>)
    : LootTableProvider(packOutput, emptySet(), listOf(SubProviderEntry(::ModLoot, LootContextParamSets.BLOCK)), provider) {

    class ModLoot(provider: HolderLookup.Provider): BlockLootSubProvider(emptySet(), FeatureFlags.REGISTRY.allFlags(), provider) {
        override fun generate() {
            dropSelf(ModBlocks.timedoorMarker)
            dropSelf(ModBlocks.chronomark)
            dropSelf(ModBlocks.timedoorProjector)
            dropSelf(ModBlocks.workstation)
            dropSelf(ModBlocks.metronome)
        }
    }
}