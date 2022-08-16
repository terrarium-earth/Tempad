package me.codexadrian.tempad.registry.forge;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.blocks.ExtraDataMenuProvider;
import me.codexadrian.tempad.registry.TempadMenus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class TempadMenusImpl {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MODID);

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String name, Supplier<MenuType<T>> menu) {
        return MENU_TYPES.register(name, menu);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> createMenuType(TempadMenus.MenuFactory<T> factory) {
        return IForgeMenuType.create(factory::create);
    }

    public static void openMenu(ServerPlayer player, ExtraDataMenuProvider provider) {
        NetworkHooks.openScreen(player, provider);
    }
}
