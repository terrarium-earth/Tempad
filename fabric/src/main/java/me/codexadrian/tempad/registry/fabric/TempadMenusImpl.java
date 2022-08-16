package me.codexadrian.tempad.registry.fabric;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.blocks.ExtraDataMenuProvider;
import me.codexadrian.tempad.fabric.ExtraDataMenuProviderWrapper;
import me.codexadrian.tempad.registry.TempadMenus;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import reborncore.common.screen.builder.ScreenHandlerBuilder;

import java.util.function.Supplier;

public class TempadMenusImpl {
    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String name, Supplier<MenuType<T>> menu) {
        var registry = Registry.register(Registry.MENU, new ResourceLocation(Constants.MODID, name), menu.get());
        return () -> registry;
    }

    public static <T extends AbstractContainerMenu> MenuType<T> createMenuType(TempadMenus.MenuFactory<T> factory) {
        return new ExtendedScreenHandlerType<>(factory::create);
    }

    public static void openMenu(ServerPlayer player, ExtraDataMenuProvider provider) {
        player.openMenu(new ExtraDataMenuProviderWrapper(provider));
    }
}
