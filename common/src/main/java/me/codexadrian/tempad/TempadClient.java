package me.codexadrian.tempad;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.client.gui.TempadScreen;
import me.codexadrian.tempad.items.TempadItem;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.util.List;

public class TempadClient {
    private static TempadClientConfig clientConfig;
    private static final List<String> incompatibleMods = List.of("flywheel", "imm_ptl_core", "structurize");

    public static void init() {
        ClampedItemPropertyFunction clampedItemPropertyFunction = (itemStack, clientLevel, livingEntity, i) -> {
            if (livingEntity instanceof Player player && itemStack.getItem() instanceof TempadItem tempad) {
                return tempad.getOption().canTimedoorOpen(player, itemStack) ? 1.0F : 0.0F;
            }
            return 0.0F;
        };

        registerItemProperty(Services.REGISTRY.getItem(), new ResourceLocation("usable"), clampedItemPropertyFunction);
        registerItemProperty(Services.REGISTRY.getCreativeItem(), new ResourceLocation("usable"), clampedItemPropertyFunction);

        try {
            clientConfig = TempadClientConfig.loadConfig(Services.PLATFORM.getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TempadClientConfig getClientConfig() {
        return clientConfig;
    }

    public static void openScreen(InteractionHand interactionHand) {
        //int color = ColorDataComponent.COLOR_DATA.get(player).getColor();
        int color = TempadClient.getClientConfig().getColor();
        Minecraft.getInstance().setScreen(new TempadScreen(color, interactionHand));
    }

    public static boolean isIncompatibleModLoaded() {
        return incompatibleMods.stream().anyMatch(Services.PLATFORM::isModLoaded);
    }

    @ExpectPlatform
    public static void registerItemProperty(Item pItem, ResourceLocation pName, ClampedItemPropertyFunction pProperty) {
    }
}
