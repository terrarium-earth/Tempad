package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.registries.ModTags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.EntityTypeTagsProvider
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.common.Tags.EntityTypes
import java.util.concurrent.CompletableFuture

class ModEntityTags(output: PackOutput, provider: CompletableFuture<HolderLookup.Provider>):
    EntityTypeTagsProvider(output, provider, Tempad.MOD_ID) {

    override fun addTags(provider: HolderLookup.Provider) {
        tag(ModTags.teleportingNotSupport)
            .addTag(EntityTypes.BOSSES)
            .addTag(EntityTypes.TELEPORTING_NOT_SUPPORTED)
            .add(EntityType.END_CRYSTAL)

        tag(EntityTypes.TELEPORTING_NOT_SUPPORTED)
            .add(ModEntities.timedoor)
    }
}