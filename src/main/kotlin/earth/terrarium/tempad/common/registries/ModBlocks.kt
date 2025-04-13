package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.block.MetronomeBlock
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBlock
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

object ModBlocks {
    val blocks = ResourcefulRegistries.create(BuiltInRegistries.BLOCK, Tempad.MOD_ID)
    val blockEntities = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Tempad.MOD_ID)

    val timedoorProjector by blocks.register("timedoor_projector", ::RudimentaryTempadBlock)

    val timedoorProjectorBE by blockEntities.register("timedoor_projector") {
        BlockEntityType.Builder.of(::RudimentaryTempadBE, timedoorProjector).build(null)
    }

    val timedoorMarker by blocks.register("timedoor_marker", ::TimedoorMarkerBlock)

    val timedoorMarkerBE by blockEntities.register("timedoor_marker") {
        BlockEntityType.Builder.of(::TimedoorMarkerBE, timedoorMarker).build(null)
    }

    val chronomark by blocks.register("chronomark", ::ChronomarkBlock)

    val chronomarkBE by blockEntities.register("chronomark") {
        BlockEntityType.Builder.of(::ChronomarkBE, chronomark).build(null)
    }

    val workstation by blocks.register("workstation", ::WorkstationBlock)

    val workstationBE by blockEntities.register("workstation") {
        BlockEntityType.Builder.of(::WorkstationBE, workstation).build(null)
    }

    val workstationChild by blocks.register("workstation_child", ::WorkstationChildBlock)

    val metronome by blocks.register("metronome", ::MetronomeBlock)

    val metronomeBe by blockEntities.register("metronome") {
        BlockEntityType.Builder.of(::MetronomeBe, metronome).build(null)
    }
}