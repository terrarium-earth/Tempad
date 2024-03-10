package me.codexadrian.tempad.common;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.common.compat.botarium.BotariumTempadOptionRegistry;
import me.codexadrian.tempad.common.compat.prometheus.PrometheusLocationProvider;
import me.codexadrian.tempad.common.compat.waystones.WaystoneLocationProvider;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.locations.TempadLocationProvider;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.registry.*;
import me.codexadrian.tempad.common.utils.PlatformUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Tempad {
    public static final String MODID = "tempad";
    public static final String MOD_NAME = "Tempad";
    public static final String TIMER_NBT = "timer";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final int ORANGE = 0xFF_ff6f00;

    public static final Configurator CONFIGURATOR = new Configurator(MODID);

    public static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "main"));

    public static final TagKey<Item> TEMPAD_FUEL_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(MODID, "tempad_fuel"));
    public static final TagKey<Fluid> TEMPAD_LIQUID_FUEL_TAG = TagKey.create(Registries.FLUID, new ResourceLocation(MODID, "tempad_liquid_fuel"));
    public static final TagKey<Level> TEMPAD_DIMENSION_BLACKLIST = TagKey.create(Registries.DIMENSION, new ResourceLocation(MODID, "tempad_dimension_blacklist"));
    public static final TagKey<EntityType<?>> TEMPAD_ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, "tempad_entity_blacklist"));
    public static final TagKey<EntityType<?>> TEMPAD_ENTITY_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, "tempad_entity_whitelist"));
    public static final Supplier<SoundEvent> TIMEDOOR_SOUND = registerSound("entity.timedoor.open");

    public static void init() {
        TempadOptions.init();
        TempadLocationProvider.init();

        if (PlatformUtils.isModLoaded("waystones")) {
            WaystoneLocationProvider.init();
        }

        if (PlatformUtils.isModLoaded("prometheus")) {
            PrometheusLocationProvider.init();
        }

        CONFIGURATOR.register(TempadConfig.class);
        TempadBlocks.REGISTRY.init();
        TempadBlockEntities.REGISTRY.init();
        TempadMenus.REGISTRY.init();
        TempadRegistry.ITEMS.init();
        TempadRegistry.ENTITIES.init();
        TempadRegistry.ITEM_GROUP.init();
        NetworkHandler.register();

        if (PlatformUtils.isModLoaded("botarium")) {
            BotariumTempadOptionRegistry.postInit();
        }
    }

    @ExpectPlatform
    public static Supplier<SoundEvent> registerSound(String name) {
        throw new AssertionError("Not implemented");
    }
}
