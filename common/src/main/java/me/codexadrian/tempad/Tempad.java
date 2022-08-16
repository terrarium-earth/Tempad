package me.codexadrian.tempad;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.platform.Services;
import me.codexadrian.tempad.registry.TempadBlocks;
import me.codexadrian.tempad.registry.TempadEntities;
import me.codexadrian.tempad.registry.TempadItems;
import me.codexadrian.tempad.registry.TempadMenus;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.util.function.Supplier;

public class Tempad {

    private static TempadConfig tempadConfig;

    public static final TagKey<Item> TEMPAD_FUEL_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Constants.MODID, "tempad_fuel"));

    public static final Supplier<SoundEvent> TIMEDOOR_SOUND = registerSound("entity.timedoor.open");

    public static void init() {
        try {
            tempadConfig = TempadConfig.loadConfig(Services.PLATFORM.getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TempadBlocks.register();
        TempadItems.register();
        TempadEntities.register();
        TempadMenus.register();
    }

    public static TempadConfig getTempadConfig() {
        return tempadConfig;
    }

    @ExpectPlatform
    public static Supplier<SoundEvent> registerSound(String name) {
        throw new AssertionError("Not implemented");
    }
}
