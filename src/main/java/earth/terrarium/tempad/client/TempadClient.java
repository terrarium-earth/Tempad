package earth.terrarium.tempad.client;

import earth.terrarium.tempad.api.apps.TempadAppApi;
import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.options.FuelOptionsApi;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.api.power.PowerSettingsApi;
import earth.terrarium.tempad.client.apps.impl.NewLocationApp;
import earth.terrarium.tempad.client.apps.impl.SettingsApp;
import earth.terrarium.tempad.client.apps.impl.TeleportApp;
import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.client.screens.PrinterScreen;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.registry.TempadBlocks;
import earth.terrarium.tempad.common.registry.TempadMenus;
import earth.terrarium.tempad.common.registry.TempadRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TempadClient {
    private static final ClampedItemPropertyFunction CLAMPED_ITEM_PROPERTY_FUNCTION = (itemStack, clientLevel, livingEntity, i) -> {
        if (livingEntity instanceof Player player) {
            FuelOption option = FuelOptionsApi.API.findItemOption(itemStack);
            PowerSettings attachment = PowerSettingsApi.API.get(itemStack);
            return option.canTimedoorOpen(itemStack, attachment, player) ? 1.0F : 0.0F;
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

    public static void registerItemProperty(Item pItem, ResourceLocation pName, ClampedItemPropertyFunction pProperty) {
        ItemProperties.register(pItem, pName, pProperty);
    }

    public static void registerRenderLayer(Block block, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }
}
