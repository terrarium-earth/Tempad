package me.codexadrian.tempad;

import me.codexadrian.tempad.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.io.IOException;

public class Tempad {

    private static TempadConfig tempadConfig;

    public static final TagKey<Item> TEMPAD_FUEL_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Constants.MODID, "tempad_fuel"));

    public static void init() {
        try {
            tempadConfig = TempadConfig.loadConfig(Services.PLATFORM.getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TempadConfig getTempadConfig() {
        return tempadConfig;
    }
}
