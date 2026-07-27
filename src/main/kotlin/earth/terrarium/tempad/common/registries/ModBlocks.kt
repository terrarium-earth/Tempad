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
import earth.terrarium.tempad.common.block.TemputerTimeSteelBE
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.block.WorkstationBlock
import earth.terrarium.tempad.common.block.TemputerTimeSteelBlock
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

    val temputerIron by blocks.register("temputer_iron", ::RudimentaryTempadBlock) { Properties.of().strength(3.0f, 6.0f) }

    val temputerIronBE by blockEntities.register("temputer_iron") {
        BlockEntityType(::RudimentaryTempadBE, temputerIron)
    }

    val doorpointIron by blocks.register("doorpoint_iron", ::TimedoorMarkerBlock) { Properties.of().strength(3.0f, 6.0f) }

    val doorpointIronBE by blockEntities.register("doorpoint_iron") {
        BlockEntityType(::TimedoorMarkerBE, doorpointIron)
    }

    val doorpointTimeSteel by blocks.register("doorpoint_time_steel", ::ChronomarkBlock) { Properties.of().strength(3.0f, 6.0f) }

    val doorpointTimeSteelBE by blockEntities.register("doorpoint_time_steel") {
        BlockEntityType(::ChronomarkBE, doorpointTimeSteel)
    }

    val workstation by blocks.register("workstation", ::WorkstationBlock) { Properties.of().noOcclusion().strength(3.0f, 1200f) }

    val workstationBE by blockEntities.register("workstation") {
        BlockEntityType(::WorkstationBE, workstation)
    }

    val temputerTimeSteel by blocks.register("temputer_time_steel", ::TemputerTimeSteelBlock) { Properties.of().strength(3.0f, 1200f) }

    val temputerTimeSteelBE by blockEntities.register("temputer_time_steel") {
        BlockEntityType(::TemputerTimeSteelBE, temputerTimeSteel)
    }

    val metronome by blocks.register("metronome", ::MetronomeBlock) { Properties.of().strength(3.0f, 1200f) }

    val metronomeBE by blockEntities.register("metronome") {
        BlockEntityType(::MetronomeBe, metronome)
    }

    val liftway by blocks.register("liftway", ::LiftwayBlock) { Properties.of().strength(3.0f, 6.0f) }

    val liftwayBE by blockEntities.register("liftway") {
        BlockEntityType(::LiftwayBe, liftway)
    }
}