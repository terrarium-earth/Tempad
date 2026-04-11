package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.packs.VanillaLootTableProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Function

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

        override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
            generate()
            ModBlocks.timedoorMarker.register(output)
            ModBlocks.chronomark.register(output)
            ModBlocks.timedoorProjector.register(output)
            ModBlocks.workstation.register(output)
            ModBlocks.metronome.register(output)
        }

        fun Block.register(pGenerator: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
            val Identifier: ResourceKey<LootTable> = this.getLootTable()
            if (Identifier !== BuiltInLootTables.EMPTY) {
                map.remove(Identifier)?.let { pGenerator.accept(Identifier, it) }
            }
        }
    }
}