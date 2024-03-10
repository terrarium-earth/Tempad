package me.codexadrian.tempad.common.options;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.options.impl.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FuelOptionsApiImpl implements FuelOptionsApi {
    private final Map<ResourceLocation, FuelOption> OPTION_REGISTRY = new HashMap<>();

    @Override
    public int getFuelCost(ItemStack stack) {
        if (stack.getItem() instanceof TempadItem tempadItem) {
            return tempadItem.getFuelCost();
        } else {
            return 0;
        }
    }

    @Override
    public int getFuelCapacity(ItemStack stack) {
        if (stack.getItem() instanceof TempadItem tempadItem) {
            return tempadItem.getFuelCapacity();
        } else {
            return 0;
        }
    }

    @Override
    public void register(ResourceLocation id, FuelOption option) {
        OPTION_REGISTRY.put(id, option);
    }

    @Override
    public FuelOption getOption(ResourceLocation id) {
        return OPTION_REGISTRY.get(id);
    }

    @Override
    public FuelOption getSelected(ItemStack stack) {
        return null;
    }

    @Override
    public ResourceLocation getSelectedId(ItemStack stack) {
        return null;
    }

    @Override
    public Map<ResourceLocation, FuelOption> getOptions() {
        return OPTION_REGISTRY;
    }
}
