package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.LiftwayBe
import earth.terrarium.tempad.common.block.LiftwayBlock
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.block.MetronomeBlock
import earth.terrarium.tempad.common.block.RudimentaryTempadBlock
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.block.WorkstationBlock
import earth.terrarium.tempad.common.block.WorkstationChildBlock
import earth.terrarium.tempad.common.block.timedoor_marker.ChronomarkBE
import earth.terrarium.tempad.common.block.timedoor_marker.ChronomarkBlock
import earth.terrarium.tempad.common.block.timedoor_marker.TimedoorMarkerBE
import earth.terrarium.tempad.common.block.timedoor_marker.TimedoorMarkerBlock
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties

object ModBlocks {
    val blocks = ResourcefulRegistries.createForBlocks(Tempad.MOD_ID)
    val blockEntities = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Tempad.MOD_ID)

    val timedoorProjector by blocks.register("timedoor_projector", ::RudimentaryTempadBlock) { Properties.of().strength(3.0f, 6.0f) }

    val timedoorProjectorBE by blockEntities.register("timedoor_projector") {
        BlockEntityType(::RudimentaryTempadBE, timedoorProjector)
    }

    val timedoorMarker by blocks.register("timedoor_marker", ::TimedoorMarkerBlock) { Properties.of().strength(3.0f, 6.0f) }

    val timedoorMarkerBE by blockEntities.register("timedoor_marker") {
        BlockEntityType(::TimedoorMarkerBE, timedoorMarker)
    }

    val chronomark by blocks.register("chronomark", ::ChronomarkBlock) { Properties.of().strength(3.0f, 6.0f) }

    val chronomarkBE by blockEntities.register("chronomark") {
        BlockEntityType(::ChronomarkBE, chronomark)
    }

    val workstation by blocks.register("workstation", ::WorkstationBlock) { Properties.of().noOcclusion().strength(3.0f, 1200f) }

    val workstationBE by blockEntities.register("workstation") {
        BlockEntityType(::WorkstationBE, workstation)
    }

    val workstationChild by blocks.register("workstation_child", ::WorkstationChildBlock) { Properties.of().strength(3.0f, 1200f) }

    val metronome by blocks.register("metronome", ::MetronomeBlock) { Properties.of().strength(3.0f, 1200f) }

    val metronomeBE by blockEntities.register("metronome") {
        BlockEntityType(::MetronomeBe, metronome)
    }

    val liftway by blocks.register("liftway", ::LiftwayBlock) { Properties.of().strength(3.0f, 6.0f) }

    val liftwayBE by blockEntities.register("liftway") {
        BlockEntityType(::LiftwayBe, liftway)
    }
}