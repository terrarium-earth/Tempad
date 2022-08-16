package me.codexadrian.tempad.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.blocks.ExtraDataMenuProvider;
import me.codexadrian.tempad.blocks.TempadBlockMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class TempadMenus {

    public static final Supplier<MenuType<TempadBlockMenu>> TEMPAD_BLOCK_MENU = registerMenu("tempad_block_menu", () -> createMenuType(TempadBlockMenu::new));

    @ExpectPlatform
    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String name, Supplier<MenuType<T>> menu) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void openMenu(ServerPlayer player, ExtraDataMenuProvider provider) {
        throw new AssertionError();
    }

    public static void register() {

    }

    @FunctionalInterface
    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int syncId, Inventory inventory, FriendlyByteBuf byteBuf);
    }
}
