package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.ModTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import java.util.concurrent.CompletableFuture

class ModItemTags(output: PackOutput,
                  lookupProvider: CompletableFuture<HolderLookup.Provider>):
    IntrinsicHolderTagsProvider<Item>(output, Registries.ITEM, lookupProvider, { item -> BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow() }, Tempad.MOD_ID) {

    val String.curios: TagKey<Item> get() = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", this))

    override fun addTags(provider: HolderLookup.Provider) {
        tag("bracelet".curios)
            .add(ModItems.tempad)
            .add(ModItems.chrononGenIron)
            .add(ModItems.chrononGenTimeSteel)
            .add(ModItems.creativeChronometer)

        tag("belt".curios)
            .add(ModItems.tempad)
            .add(ModItems.cardWallet)

        tag("charm".curios)
            .add(ModItems.capacitorTimeSteel)
            .add(ModItems.capacitorIron)
            .add(ModItems.chrononGenIron)
            .add(ModItems.chrononGenTimeSteel)
            .add(ModItems.creativeChronometer)
            .add(ModItems.waymitterIron)
            .add(ModItems.waymitterTimeSteel)
            .add(ModItems.cardWallet)
            .add(ModItems.creativeChronometer)

        tag(ModTags.chargeBlacklist)
            .add(ModItems.metronome)
            .add(ModItems.chrononGenTimeSteel)
            .add(ModItems.chrononGenIron)

        tag(ModTags.batteries)
            .add(ModItems.chrononGenTimeSteel)
            .add(ModItems.chrononGenIron)
            .add(ModItems.capacitorIron)
            .add(ModItems.capacitorTimeSteel)

        tag(ModTags.chrononGens)
            .add(ModItems.chrononGenTimeSteel)
            .add(ModItems.chrononGenIron)
    }
}