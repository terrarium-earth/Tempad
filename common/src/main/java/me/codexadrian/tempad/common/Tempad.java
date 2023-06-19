package me.codexadrian.tempad.common;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.common.network.NetworkHandler;

import me.codexadrian.tempad.common.utils.PlatformUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Supplier;

public class Tempad {
    public static final String MODID = "tempad";
    public static final String MOD_NAME = "Tempad";
    public static final String TIMER_NBT = "timer";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final int ORANGE = 0xFF_ff6f00;

    private static TempadConfig tempadConfig;

    public static final TagKey<Item> TEMPAD_FUEL_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(MODID, "tempad_fuel"));
    public static final Supplier<SoundEvent> TIMEDOOR_SOUND = registerSound("entity.timedoor.open");

    public static void init() {
        NetworkHandler.register();
        try {
            tempadConfig = TempadConfig.loadConfig(PlatformUtils.getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TempadConfig getTempadConfig() {
        return tempadConfig;
    }

    @ExpectPlatform
    public static Supplier<SoundEvent> registerSound(String name) {
        throw new AssertionError("Not implemented");
    }
}
