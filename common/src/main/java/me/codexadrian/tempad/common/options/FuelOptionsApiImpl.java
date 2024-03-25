package me.codexadrian.tempad.common.options;

import earth.terrarium.botarium.common.generic.LookupApi;
import earth.terrarium.botarium.common.generic.base.BlockContainerLookup;
import earth.terrarium.botarium.common.generic.base.ItemContainerLookup;
import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FuelOptionsApiImpl implements FuelOptionsApi {
    private final Map<ResourceLocation, FuelOption> OPTION_REGISTRY = new HashMap<>();
    private final ItemContainerLookup<FuelOption, Void> ITEMS = LookupApi.createItemLookup(new ResourceLocation(Tempad.MODID, "fuel_options"), FuelOption.class);
    private final BlockContainerLookup<FuelOption, Void> BLOCKS = LookupApi.createBlockLookup(new ResourceLocation(Tempad.MODID, "fuel_options"), FuelOption.class, Void.class);

    @Override
    public void register(ResourceLocation id, FuelOption option) {
        OPTION_REGISTRY.put(id, option);
    }

    @Override
    public FuelOption getOption(ResourceLocation id) {
        return OPTION_REGISTRY.get(id);
    }

    @Override
    public void attachItemOption(ItemContainerLookup.ItemGetter<FuelOption, Void> getter, Supplier<Item>... items) {
        ITEMS.registerItems(getter, items);
    }

    @Override
    public FuelOption findItemOption(ItemStack stack) {
        return ITEMS.find(stack, null);
    }

    @Override
    public void attachBlockOptions(BlockContainerLookup.BlockGetter<FuelOption, Void> getter, Supplier<Block>... items) {
        BLOCKS.registerBlocks(getter, items);
    }

    @Override
    public void attachBEOptions(BlockContainerLookup.BlockGetter<FuelOption, Void> getter, Supplier<BlockEntityType<?>>... items) {
        BLOCKS.registerBlockEntities(getter, items);
    }

    @Override
    public FuelOption findBlockOption(Level level, BlockPos pos, BlockState state, BlockEntity entity) {
        return BLOCKS.find(level, pos, state, entity, null);
    }

    @Override
    public FuelOption findBlockOption(Level level, BlockPos pos) {
        return BLOCKS.find(level, pos, null);
    }

    @Override
    public FuelOption findBlockOption(BlockEntity entity) {
        return BLOCKS.find(entity, null);
    }
}
