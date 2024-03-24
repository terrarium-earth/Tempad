package me.codexadrian.tempad.client;

import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.api.apps.TempadAppApi;
import me.codexadrian.tempad.client.apps.impl.NewLocationApp;
import me.codexadrian.tempad.client.apps.impl.SettingsApp;
import me.codexadrian.tempad.client.apps.impl.TeleportApp;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.screens.PrinterScreen;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.registry.TempadBlocks;
import me.codexadrian.tempad.common.registry.TempadMenus;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TempadClient {
    private static final ClampedItemPropertyFunction CLAMPED_ITEM_PROPERTY_FUNCTION = (itemStack, clientLevel, livingEntity, i) -> {
        if (livingEntity instanceof Player player && itemStack.getItem() instanceof TempadItem tempad) {
            return tempad.getOption().canTimedoorOpen(player, itemStack) ? 1.0F : 0.0F;
        }
        return 0.0F;
    };

    public static void init() {
        Tempad.CONFIGURATOR.register(TempadClientConfig.class);
        MenuScreens.register(TempadMenus.PRINTER.get(), PrinterScreen::new);

        TempadAppApi.API.register(TeleportApp.ID, TeleportApp.INSTANCE);
        TempadAppApi.API.register(NewLocationApp.ID, NewLocationApp.INSTANCE);
        TempadAppApi.API.register(SettingsApp.ID, SettingsApp.INSTANCE);
        TempadAppApi.API.setHomePageId(TeleportApp.ID);

        registerRenderLayer(TempadBlocks.LOCATION_TERMINAL.get(), RenderType.cutout());
    }

    public static void initItemProperties() {
        registerItemProperty(TempadRegistry.TEMPAD.get(), new ResourceLocation("usable"), CLAMPED_ITEM_PROPERTY_FUNCTION);
        registerItemProperty(TempadRegistry.CREATIVE_TEMPAD.get(), new ResourceLocation("usable"), CLAMPED_ITEM_PROPERTY_FUNCTION);
    }

    @ExpectPlatform
    public static void registerItemProperty(Item pItem, ResourceLocation pName, ClampedItemPropertyFunction pProperty) {}

    @ExpectPlatform
    public static void registerRenderLayer(Block block, RenderType renderType) {}
}
