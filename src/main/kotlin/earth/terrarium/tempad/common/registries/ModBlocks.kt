package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.RudimentaryTempadBlock
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType

object ModBlocks {
    val blocks = ResourcefulRegistries.create(BuiltInRegistries.BLOCK, Tempad.MOD_ID)
    val blockEntities = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Tempad.MOD_ID)

    val rudimentaryTempad by blocks.register("rudimentary_tempad") {
        RudimentaryTempadBlock()
    }

    val rudimentaryTempadBE by blockEntities.register("rudimentary_tempad") {
        BlockEntityType.Builder.of(::RudimentaryTempadBE, rudimentaryTempad).build(null)
    }
}