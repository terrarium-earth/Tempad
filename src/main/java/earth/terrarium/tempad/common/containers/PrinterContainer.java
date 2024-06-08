package earth.terrarium.tempad.common.containers;

import earth.terrarium.botarium.common.item.impl.SimpleItemContainer;
import earth.terrarium.tempad.common.registry.TempadRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PrinterContainer extends SimpleItemContainer {


    public PrinterContainer() {
        super(2);
    }

    public PrinterContainer(ItemStack stack) {
        super(2, stack);
    }

    public PrinterContainer(Level level, BlockPos blockPos) {
        super(2, level, blockPos);
    }



    @Override
    public int getSlotLimit(int slot) {
        return slot == 0 ? 64 : 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(TempadRegistry.LOCATION_CARD.get());
    }
}
