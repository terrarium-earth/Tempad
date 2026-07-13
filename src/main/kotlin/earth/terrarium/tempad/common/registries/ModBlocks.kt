package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
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

object ModBlocks {
    val blocks = ResourcefulRegistries.create(BuiltInRegistries.BLOCK, Tempad.MOD_ID)
    val blockEntities = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Tempad.MOD_ID)

    val timedoorProjector by blocks.register("timedoor_projector", ::RudimentaryTempadBlock)

    val timedoorProjectorBE by blockEntities.register("timedoor_projector") {
        BlockEntityType(::RudimentaryTempadBE, timedoorProjector)
    }

    val timedoorMarker by blocks.register("timedoor_marker", ::TimedoorMarkerBlock)

    val timedoorMarkerBE by blockEntities.register("timedoor_marker") {
        BlockEntityType(::TimedoorMarkerBE, timedoorMarker)
    }

    val chronomark by blocks.register("chronomark", ::ChronomarkBlock)

    val chronomarkBE by blockEntities.register("chronomark") {
        BlockEntityType(::ChronomarkBE, chronomark)
    }

    val workstation by blocks.register("workstation", ::WorkstationBlock)

    val workstationBE by blockEntities.register("workstation") {
        BlockEntityType(::WorkstationBE, workstation)
    }

    val workstationChild by blocks.register("workstation_child", ::WorkstationChildBlock)

    val metronome by blocks.register("metronome", ::MetronomeBlock)

    val metronomeBe by blockEntities.register("metronome") {
        BlockEntityType(::MetronomeBe, metronome)
    }
}