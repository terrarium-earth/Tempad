package earth.terrarium.tempad.data.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.RudimentaryTempadBlock
import earth.terrarium.tempad.common.block.WorkstationBlock
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.tempadId
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.renderer.block.dispatch.Variant
import net.minecraft.core.Holder
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.stream.Stream

class ModBlockStateData(output: PackOutput) : ModelProvider(output, Tempad.MOD_ID) {

    override fun getKnownItems(): Stream<out Holder<Item>> = Stream.empty()
    override fun getKnownBlocks(): Stream<out Holder<Block>> = Stream.empty()

    override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {
        // timedoor_projector: HORIZONTAL_FACING × TRIGGERED × has_card
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(ModBlocks.timedoorProjector)
                .with(
                    PropertyDispatch.initial(
                        BlockStateProperties.TRIGGERED,
                        RudimentaryTempadBlock.hasCardProperty,
                    ).generate { triggered, hasCard ->
                        val model = "block/timedoor_projector_${if (triggered) "on" else "off"}${if (hasCard) "_with_card" else ""}".tempadId
                        BlockModelGenerators.variant(Variant(model))
                    },
                )
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING),
        )

        // workstation: HORIZONTAL_FACING × HAS_TAPE
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(ModBlocks.workstation)
                .with(
                    PropertyDispatch.initial(WorkstationBlock.HAS_TAPE)
                        .select(true, BlockModelGenerators.variant(Variant("tempad:block/workstation_with_tape".tempadId)))
                        .select(false, BlockModelGenerators.variant(Variant("tempad:block/workstation".tempadId))),
                )
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING),
        )

        // workstationChild: HORIZONTAL_FACING only (TRIGGERED ignored)
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(
                ModBlocks.workstationChild,
                BlockModelGenerators.variant(Variant("tempad:block/workstation_terminal".tempadId)),
            )
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING),
        )

        // metronome: no properties
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(
                ModBlocks.metronome,
                BlockModelGenerators.plainVariant("tempad:block/metronome".tempadId),
            ),
        )
    }
}
