package earth.terrarium.tempad.common.blocks;

import earth.terrarium.botarium.common.item.ItemContainerBlock;
import earth.terrarium.botarium.common.item.SerializableContainer;
import earth.terrarium.botarium.common.item.SimpleItemContainer;
import earth.terrarium.tempad.common.registry.TempadBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LocationTerminalBlockEntity extends BlockEntity implements ItemContainerBlock {
    private final SerializableContainer container = new SimpleItemContainer(this, 1);

    public LocationTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(TempadBlockEntities.LOCATION_TERMINAL.get(), pos, blockState);
    }

    @Override
    public SerializableContainer getContainer() {
        return container;
    }
}
