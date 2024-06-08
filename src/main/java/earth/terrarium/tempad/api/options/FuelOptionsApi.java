package earth.terrarium.tempad.api.options;

import earth.terrarium.botarium.common.generic.base.BlockContainerLookup;
import earth.terrarium.botarium.common.generic.base.ItemContainerLookup;
import earth.terrarium.tempad.api.ApiHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public interface FuelOptionsApi {
    FuelOptionsApi API = ApiHelper.load(FuelOptionsApi.class);

    /**
     * Registers a TempadOption with the given identifier.
     *
     * @param id     The resource location identifier for the TempadOption.
     * @param option The TempadOption to register.
     */
    void register(ResourceLocation id, FuelOption option);

    /**
     * Retrieves a TempadOption with the specified resource location identifier.
     *
     * @param id The resource location identifier of the TempadOption to retrieve.
     * @return The TempadOption with the specified resource location identifier.
     */
    FuelOption getOption(ResourceLocation id);

    void attachItemOption(ItemContainerLookup.ItemGetter<FuelOption, Void> getter, Supplier<Item> ... items);

    FuelOption findItemOption(ItemStack stack);

    void attachBlockOptions(BlockContainerLookup.BlockGetter<FuelOption, Void> getter, Supplier<Block> ... items);

    void attachBEOptions(BlockContainerLookup.BlockGetter<FuelOption, Void> getter, Supplier<BlockEntityType<?>> ... items);

    FuelOption findBlockOption(Level level, BlockPos pos, BlockState state, BlockEntity entity);

    FuelOption findBlockOption(Level level, BlockPos pos);

    FuelOption findBlockOption(BlockEntity entity);
}
