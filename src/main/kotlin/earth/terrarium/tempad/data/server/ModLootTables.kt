package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import java.util.concurrent.CompletableFuture

class ModLootTables(packOutput: PackOutput, provider: CompletableFuture<HolderLookup.Provider>) : LootTableProvider(
    packOutput,
    emptySet(),
    listOf(SubProviderEntry(::ModLoot, LootContextParamSets.BLOCK)),
    provider
) {

    class ModLoot(provider: HolderLookup.Provider) :
        BlockLootSubProvider(emptySet(), FeatureFlags.REGISTRY.allFlags(), provider) {
        override fun getKnownBlocks(): Iterable<Block> {
            return ModBlocks.blocks.entries.map { it.get() }.filter { it != ModBlocks.temputerTimeSteel }
        }

        override fun generate() {
            dropSelf(ModBlocks.doorpointIron)
            dropSelf(ModBlocks.doorpointTimeSteel)
            add(
                ModBlocks.temputerIron, LootTable.lootTable()
                    .withPool(
                        this.applyExplosionCondition(
                            ModBlocks.temputerIron, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                                LootItem.lootTableItem(ModBlocks.temputerIron)
                                    .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY))
                            )
                        )
                    )
            )
            dropSelf(ModBlocks.workstation)
            dropSelf(ModBlocks.metronome)
            dropSelf(ModBlocks.liftway)
        }
    }
}