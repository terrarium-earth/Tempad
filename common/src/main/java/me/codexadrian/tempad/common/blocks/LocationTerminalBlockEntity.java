package me.codexadrian.tempad.common.blocks;

import me.codexadrian.tempad.common.registry.TempadBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LocationTerminalBlockEntity extends BlockEntity {
    public LocationTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(TempadBlockEntities.LOCATION_TERMINAL.get(), pos, blockState);
    }
}
