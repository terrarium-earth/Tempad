package earth.terrarium.tempad.common.containers;

import earth.terrarium.botarium.common.item.base.ItemContainer;
import earth.terrarium.botarium.common.item.impl.SimpleItemContainer;
import earth.terrarium.tempad.common.registry.TempadRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrudeTempadContainer extends SimpleItemContainer {

    public CrudeTempadContainer(ItemStack stack) {
        super(1, stack);
    }

    public CrudeTempadContainer(Level level, BlockPos blockPos) {
        super(1, level, blockPos);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(TempadRegistry.LOCATION_CARD.get());
    }
}
