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
import net.neoforged.neoforge.common.Tags
import java.util.concurrent.CompletableFuture

class ModItemTags(output: PackOutput,
                  lookupProvider: CompletableFuture<HolderLookup.Provider>):
    IntrinsicHolderTagsProvider<Item>(output, Registries.ITEM, lookupProvider, { item -> BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow() }, Tempad.MOD_ID) {

    val String.curios: TagKey<Item> get() = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", this))

    override fun addTags(provider: HolderLookup.Provider) {
        tag("bracelet".curios)
            .add(ModItems.tempad)
            .add(ModItems.chrononGenerator)
            .add(ModItems.chronometer)
            .add(ModItems.creativeChronometer)

        tag("belt".curios)
            .add(ModItems.tempad)
            .add(ModItems.cardWallet)

        tag("charm".curios)
            .add(ModItems.chrononBattery)
            .add(ModItems.chrononCell)
            .add(ModItems.chrononGenerator)
            .add(ModItems.chronometer)
            .add(ModItems.creativeChronometer)
            .add(ModItems.locationBroadcaster)
            .add(ModItems.screeningDevice)
            .add(ModItems.cardWallet)
            .add(ModItems.creativeChronometer)

        tag(ModTags.chargeBlacklist)
            .add(ModItems.metronome)
            .add(ModItems.chronometer)
            .add(ModItems.chrononGenerator)

        tag(ModTags.batteries)
            .add(ModItems.chronometer)
            .add(ModItems.chrononGenerator)
            .add(ModItems.chrononCell)
            .add(ModItems.chrononBattery)

        tag(ModTags.chrononGens)
            .add(ModItems.chronometer)
            .add(ModItems.chrononGenerator)
    }
}