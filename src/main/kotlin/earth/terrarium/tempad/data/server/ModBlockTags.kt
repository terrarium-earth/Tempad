package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModBlockTags(output: PackOutput,
                   lookupProvider: CompletableFuture<HolderLookup.Provider>,
                   existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(output, lookupProvider, Tempad.MOD_ID, existingFileHelper) {

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