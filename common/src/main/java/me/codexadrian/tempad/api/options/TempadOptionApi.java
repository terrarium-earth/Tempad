package me.codexadrian.tempad.api.options;

import me.codexadrian.tempad.api.options.impl.*;
import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TempadOptionApi {
    public static final Map<String, TempadOption> OPTION_REGISTRY = new HashMap<>();

    static {
        OPTION_REGISTRY.put("tempad:unlimited", new UnlimitedOption());
        OPTION_REGISTRY.put("tempad:experience_level", new ExperienceLevelOption());
        OPTION_REGISTRY.put("tempad:experience_points", new ExperiencePointsOption());
        OPTION_REGISTRY.put("tempad:item", new ItemOption());
        OPTION_REGISTRY.put("tempad:timer", new TimerOption());
    }

    public static TempadOption getOption(String id) {
        return OPTION_REGISTRY.getOrDefault(id, new TimerOption());
    }

    public static int getFuelCost(ItemStack stack) {
        if (stack.getItem() instanceof TempadItem tempadItem) {
            return tempadItem.getFuelCost();
        } else {
            return 0;
        }
    }

    public static int getFuelCapacity(ItemStack stack) {
        if (stack.getItem() instanceof TempadItem tempadItem) {
            return tempadItem.getFuelCapacity();
        } else {
            return 0;
        }
    }
}
