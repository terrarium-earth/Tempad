package earth.terrarium.tempad.data.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.RudimentaryTempadBlock
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.tempadId
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModBlockStateData(output: PackOutput, helper: ExistingFileHelper) : BlockStateProvider(output, Tempad.MOD_ID, helper) {
    override fun registerStatesAndModels() {
        getVariantBuilder(ModBlocks.rudimentaryTempad)
            .forAllStates { state: BlockState ->
                val dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                val triggered = state.getValue(BlockStateProperties.TRIGGERED)
                val hasCard = state.getValue(RudimentaryTempadBlock.hasCardProperty)

                var model = "block/rudimentary_tempad"
                model += "_${if (triggered) "on" else "off"}"
                if (hasCard) {
                    model += "_with_card"
                }
                ConfiguredModel.builder()
                    .modelFile(ModelFile.UncheckedModelFile(model.tempadId))
                    .rotationY(if (dir.axis.isVertical) 0 else ((dir.toYRot().toInt())) % 360)
                    .build()
            }
    }
}