package me.codexadrian.tempad.client;

import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.tempad.TempadItem;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;

public class TempadClient {
    private static final List<String> incompatibleMods = List.of("flywheel", "imm_ptl_core", "structurize", "mahoutsukai");
    private static final ClampedItemPropertyFunction CLAMPED_ITEM_PROPERTY_FUNCTION = (itemStack, clientLevel, livingEntity, i) -> {
        if (livingEntity instanceof Player player && itemStack.getItem() instanceof TempadItem tempad) {
            return tempad.getOption().canTimedoorOpen(player, itemStack) ? 1.0F : 0.0F;
        }
        return 0.0F;
    };

    public static void init() {
        Tempad.CONFIGURATOR.registerConfig(TempadClientConfig.class);
    }

    public static void initItemProperties() {
        registerItemProperty(TempadRegistry.TEMPAD.get(), new ResourceLocation("usable"), CLAMPED_ITEM_PROPERTY_FUNCTION);
        registerItemProperty(TempadRegistry.CREATIVE_TEMPAD.get(), new ResourceLocation("usable"), CLAMPED_ITEM_PROPERTY_FUNCTION);
    }

    @ExpectPlatform
    public static void registerItemProperty(Item pItem, ResourceLocation pName, ClampedItemPropertyFunction pProperty) {
    }

    public static boolean isIncompatibleModLoaded() {
        for (String incompatibleMod : incompatibleMods) {
            if (ModInfoUtils.isModLoaded(incompatibleMod)) {
                return true;
            }
        }
        return false;
    }
}
