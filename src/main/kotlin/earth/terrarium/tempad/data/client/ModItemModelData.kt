package earth.terrarium.tempad.data.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.model.BooleanComponentProperty
import earth.terrarium.tempad.client.model.ChrononChargeProperty
import earth.terrarium.tempad.client.model.TempadInUseProperty
import earth.terrarium.tempad.client.model.WalletFullProperty
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.tempadId
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelLocationUtils
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.HasComponent
import net.minecraft.core.Holder
import net.minecraft.data.PackOutput
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.stream.Stream

class ModItemModelData(output: PackOutput) : ModelProvider(output, Tempad.MOD_ID) {
    override fun getName(): String = "Tempad Item Definitions"

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
        blockModels.registerSimpleItemModel(ModBlocks.doorpointIron, "block/timedoor_marker".tempadId)
        blockModels.registerSimpleItemModel(ModBlocks.doorpointTimeSteel, "block/chronomark".tempadId)
        blockModels.registerSimpleItemModel(ModBlocks.metronome, "block/metronome".tempadId)
        blockModels.registerSimpleItemModel(ModBlocks.workstation, "block/workstation_full".tempadId)
        blockModels.registerSimpleItemModel(ModBlocks.liftway, "block/chronomark".tempadId)

        // timedoor_projector: conditional on has_card (portalTarget component present)
        itemModels.itemModelOutput.accept(
            ModItems.temputerIron,
            ItemModelUtils.conditional(
                HasComponent(ModComponents.portalTarget, false),
                ItemModelUtils.plainModel("block/timedoor_projector_with_card".tempadId),
                ItemModelUtils.plainModel("block/timedoor_projector".tempadId),
            ),
        )

        // location_card: conditional on written (portalTarget component present)

        itemModels.itemModelOutput.accept(
            ModItems.locationCard,
            ItemModelUtils.conditional(
                HasComponent(ModComponents.portalTarget, false),
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.locationCard,
                        "_written",
                        ModelTemplates.FLAT_ITEM
                    )
                ),
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.locationCard,
                        ModelTemplates.FLAT_ITEM
                    )
                )
            ),
        )

        // location_broadcaster: conditional on enabled (default=true)
        itemModels.itemModelOutput.accept(
            ModItems.waymitterIron,
            ItemModelUtils.conditional(
                BooleanComponentProperty.of(ModComponents.enabled, true),
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.waymitterIron,
                        "_enabled",
                        ModelTemplates.FLAT_ITEM
                    )
                ),
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.waymitterIron,
                        ModelTemplates.FLAT_ITEM
                    )
                ),
            ),
        )

        // screening_device: conditional on enabled (default=true)
        itemModels.itemModelOutput.accept(
            ModItems.waymitterTimeSteel,
            ItemModelUtils.conditional(
                BooleanComponentProperty.of(ModComponents.enabled, true),
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.waymitterTimeSteel,
                        "_enabled",
                        ModelTemplates.FLAT_ITEM
                    )
                ),
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.waymitterTimeSteel,
                        ModelTemplates.FLAT_ITEM
                    )
                ),
            ),
        )

        // card_wallet: conditional on full
        itemModels.itemModelOutput.accept(
            ModItems.cardWallet,
            ItemModelUtils.conditional(
                WalletFullProperty,
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.cardWallet,
                        "_full",
                        ModelTemplates.FLAT_ITEM
                    )
                ),
                ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(
                        ModItems.cardWallet,
                        ModelTemplates.FLAT_ITEM
                    )
                ),
            ),
        )

        // Charge-based items (range dispatch by ChrononChargeProperty)
        itemModels.itemModelOutput.accept(ModItems.capacitorIron, chargeModel(ModItems.capacitorIron, itemModels))
        itemModels.itemModelOutput.accept(ModItems.capacitorTimeSteel, chargeModel(ModItems.capacitorTimeSteel, itemModels))
        itemModels.itemModelOutput.accept(ModItems.chrononGenTimeSteel, chargeModel(ModItems.chrononGenTimeSteel, itemModels))
        itemModels.itemModelOutput.accept(ModItems.chrononGenIron, chargeModel(ModItems.chrononGenIron, itemModels))

        // tempad: nested conditional (twisterEquipped × isUsingItem × charge)
        itemModels.itemModelOutput.accept(
            ModItems.tempad,
            ItemModelUtils.conditional(
                BooleanComponentProperty.of(ModComponents.twisterEquipped, false),
                // twister attached
                ItemModelUtils.conditional(
                    TempadInUseProperty,
                    tempadChargeModel(attached = true, inUse = false, itemModels),
                    tempadChargeModel(attached = true, inUse = true, itemModels),
                ),
                // no twister
                ItemModelUtils.conditional(
                    TempadInUseProperty,
                    tempadChargeModel(attached = false, inUse = false, itemModels),
                    tempadChargeModel(attached = false, inUse = true, itemModels),
                ),
            ),
        )
    }

    private fun chargeModel(item: Item, itemModels: ItemModelGenerators): ItemModel.Unbaked {
        val layer0 = TextureMapping.getItemTexture(item)
        return ItemModelUtils.rangeSelect(
            ChrononChargeProperty,
            ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(layer0), itemModels.modelOutput)),
            ItemModelUtils.override(
                ItemModelUtils.plainModel(
                    itemModels.generateLayeredItem(
                        ModelLocationUtils.getModelLocation(item, "_1"),
                        layer0,
                        TextureMapping.getItemTexture(item, "/charge_1")
                    )
                ), 0.25f
            ),
            ItemModelUtils.override(
                ItemModelUtils.plainModel(
                    itemModels.generateLayeredItem(
                        ModelLocationUtils.getModelLocation(item, "_2"),
                        layer0,
                        TextureMapping.getItemTexture(item, "/charge_2")
                    )
                ), 0.5f
            ),
            ItemModelUtils.override(
                ItemModelUtils.plainModel(
                    itemModels.generateLayeredItem(
                        ModelLocationUtils.getModelLocation(item, "_3"),
                        layer0,
                        TextureMapping.getItemTexture(item, "/charge_3")
                    )
                ), 0.75f
            ),
            ItemModelUtils.override(
                ItemModelUtils.plainModel(
                    itemModels.generateLayeredItem(
                        ModelLocationUtils.getModelLocation(item, "_4"),
                        layer0,
                        TextureMapping.getItemTexture(item, "/charge_4")
                    )
                ), 1.0f
            ),
        )
    }

    private fun tempadChargeModel(attached: Boolean, inUse: Boolean, itemModels: ItemModelGenerators): ItemModel.Unbaked {
        val layer0 = TextureMapping.getItemTexture(ModItems.tempad, if (attached) "/base_with_twister" else "/base")

        fun chargeLayer(charge: Int): Identifier {
            val layer1 = TextureMapping.getItemTexture(ModItems.tempad, "/charge_$charge")
            val form = if (attached) "attached" else "base"
            return if (inUse) {
                itemModels.generateLayeredItem(
                    ModelLocationUtils.getModelLocation(ModItems.tempad, "/${form}/off/charge_$charge"),
                    layer0,
                    layer1
                )
            } else {
                val modelLocation = ModelLocationUtils.getModelLocation(ModItems.tempad, "/${form}/on/charge_$charge")
                itemModels.generateLayeredItem(
                    modelLocation,
                    layer0,
                    layer1,
                    TextureMapping.getItemTexture(ModItems.tempad, "/screen_on")
                )
                modelLocation
            }
        }

        return ItemModelUtils.rangeSelect(
            ChrononChargeProperty,
            ItemModelUtils.plainModel(chargeLayer(0)),
            ItemModelUtils.override(ItemModelUtils.plainModel(chargeLayer(1)), 0.25f),
            ItemModelUtils.override(ItemModelUtils.plainModel(chargeLayer(2)), 0.5f),
            ItemModelUtils.override(ItemModelUtils.plainModel(chargeLayer(3)), 0.75f),
            ItemModelUtils.override(ItemModelUtils.plainModel(chargeLayer(4)), 1.0f)
        )
    }
}
