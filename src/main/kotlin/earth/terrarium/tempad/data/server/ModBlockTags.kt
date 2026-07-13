package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture

class ModBlockTags(output: PackOutput,
                   lookupProvider: CompletableFuture<HolderLookup.Provider>
) : IntrinsicHolderTagsProvider<Block>(output, Registries.BLOCK, lookupProvider, { block -> BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow() }, Tempad.MOD_ID) {

    override fun addTags(provider: HolderLookup.Provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).apply {
            for (entry in ModBlocks.blocks.entries) {
                add(entry.get())
            }
        }

        tag(BlockTags.NEEDS_IRON_TOOL).apply {
            for (entry in ModBlocks.blocks.entries) {
                add(entry.get())
            }
        }
    }
}