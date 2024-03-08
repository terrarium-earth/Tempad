package me.codexadrian.tempad.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.botarium.common.registry.RegistryHelpers;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.menu.PrinterMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class TempadMenus {
    public static final ResourcefulRegistry<MenuType<?>> REGISTRY = ResourcefulRegistries.create(BuiltInRegistries.MENU, Tempad.MODID);

    public static final RegistryEntry<MenuType<PrinterMenu>> PRINTER = REGISTRY.register("printer", () -> RegistryHelpers.createMenuType(PrinterMenu::new));
}
