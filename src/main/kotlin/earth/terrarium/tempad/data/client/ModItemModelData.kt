package earth.terrarium.tempad.data.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.model.BooleanComponentProperty
import earth.terrarium.tempad.client.model.ChrononChargeProperty
import earth.terrarium.tempad.client.model.WalletFullProperty
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.tempadId
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.HasComponent
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem
import net.minecraft.core.Holder
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.stream.Stream

class ModItemModelData(output: PackOutput) : ModelProvider(output, Tempad.MOD_ID) {

    override fun getKnownItems(): Stream<out Holder<Item>> = Stream.empty()
    override fun getKnownBlocks(): Stream<out Holder<Block>> = Stream.empty()

    override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {
        // Simple flat items
        itemModels.generateFlatItem(ModItems.timeTwister, ModelTemplates.FLAT_ITEM)
        itemModels.generateFlatItem(ModItems.newLocationUpgrade, ModelTemplates.FLAT_ITEM)
        itemModels.generateFlatItem(ModItems.playerTeleportUpgrade, ModelTemplates.FLAT_ITEM)
        itemModels.generateFlatItem(ModItems.guideUpgrade, ModelTemplates.FLAT_ITEM)
        itemModels.generateFlatItem(ModItems.timeSteel, ModelTemplates.FLAT_ITEM)
        itemModels.generateFlatItem(ModItems.creativeChronometer, ModelTemplates.FLAT_ITEM)
        itemModels.generateFlatItem(ModItems.knowledgeProjector, ModelTemplates.FLAT_ITEM)

        // Block items — reference existing block model files
        blockModels.registerSimpleItemModel(ModBlocks.timedoorMarker, "tempad:block/timedoor_marker".tempadId)
        blockModels.registerSimpleItemModel(ModBlocks.chronomark, "tempad:block/chronomark".tempadId)
        blockModels.registerSimpleItemModel(ModBlocks.metronome, "tempad:block/metronome".tempadId)
        blockModels.registerSimpleItemModel(ModBlocks.workstation, "tempad:block/workstation_full".tempadId)

        // timedoor_projector: conditional on has_card (portalTarget component present)
        itemModels.itemModelOutput.accept(
            ModItems.timedoorProjector,
            ItemModelUtils.conditional(
                HasComponent(ModComponents.portalTarget, false),
                ItemModelUtils.plainModel("tempad:item/timedoor_projector_with_card".tempadId),
                ItemModelUtils.plainModel("tempad:item/timedoor_projector".tempadId),
            ),
        )

        // location_card: conditional on written (portalTarget component present)
        itemModels.itemModelOutput.accept(
            ModItems.locationCard,
            ItemModelUtils.conditional(
                HasComponent(ModComponents.portalTarget, false),
                ItemModelUtils.plainModel("tempad:item/location_card_written".tempadId),
                ItemModelUtils.plainModel("tempad:item/location_card".tempadId),
            ),
        )

        // location_broadcaster: conditional on enabled (default=true)
        itemModels.itemModelOutput.accept(
            ModItems.locationBroadcaster,
            ItemModelUtils.conditional(
                BooleanComponentProperty.of(ModComponents.enabled, true),
                ItemModelUtils.plainModel("tempad:item/location_broadcaster_enabled".tempadId),
                ItemModelUtils.plainModel("tempad:item/location_broadcaster".tempadId),
            ),
        )

        // screening_device: conditional on enabled (default=true)
        itemModels.itemModelOutput.accept(
            ModItems.screeningDevice,
            ItemModelUtils.conditional(
                BooleanComponentProperty.of(ModComponents.enabled, true),
                ItemModelUtils.plainModel("tempad:item/screening_device_enabled".tempadId),
                ItemModelUtils.plainModel("tempad:item/screening_device".tempadId),
            ),
        )

        // card_wallet: conditional on full
        itemModels.itemModelOutput.accept(
            ModItems.cardWallet,
            ItemModelUtils.conditional(
                WalletFullProperty,
                ItemModelUtils.plainModel("tempad:item/card_wallet_full".tempadId),
                ItemModelUtils.plainModel("tempad:item/card_wallet".tempadId),
            ),
        )

        // Charge-based items (range dispatch by ChrononChargeProperty)
        itemModels.itemModelOutput.accept(ModItems.chrononCell, chargeModel("chronon_cell"))
        itemModels.itemModelOutput.accept(ModItems.chrononBattery, chargeModel("chronon_battery"))
        itemModels.itemModelOutput.accept(ModItems.chronometer, chargeModel("chronometer"))
        itemModels.itemModelOutput.accept(ModItems.chrononGenerator, chargeModel("chronon_generator"))

        // tempad: nested conditional (twisterEquipped × isUsingItem × charge)
        itemModels.itemModelOutput.accept(
            ModItems.tempad,
            ItemModelUtils.conditional(
                BooleanComponentProperty.of(ModComponents.twisterEquipped, false),
                // twister attached
                ItemModelUtils.conditional(
                    IsUsingItem(),
                    tempadChargeModel("attached_in_use"),
                    tempadChargeModel("attached_idle"),
                ),
                // no twister
                ItemModelUtils.conditional(
                    IsUsingItem(),
                    tempadChargeModel("base_in_use"),
                    tempadChargeModel("base_idle"),
                ),
            ),
        )
    }

    private fun chargeModel(name: String): ItemModel.Unbaked =
        ItemModelUtils.rangeSelect(
            ChrononChargeProperty,
            ItemModelUtils.plainModel("tempad:item/${name}_0".tempadId),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/${name}_1".tempadId), 0.25f),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/${name}_2".tempadId), 0.5f),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/${name}_3".tempadId), 0.75f),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/${name}_4".tempadId), 1.0f),
        )

    private fun tempadChargeModel(variant: String): ItemModel.Unbaked =
        ItemModelUtils.rangeSelect(
            ChrononChargeProperty,
            ItemModelUtils.plainModel("tempad:item/tempad/${variant}_0".tempadId),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/tempad/${variant}_1".tempadId), 0.25f),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/tempad/${variant}_2".tempadId), 0.5f),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/tempad/${variant}_3".tempadId), 0.75f),
            ItemModelUtils.override(ItemModelUtils.plainModel("tempad:item/tempad/${variant}_4".tempadId), 1.0f),
        )
}
