package me.codexadrian.tempad.registry.fabric;

import me.codexadrian.tempad.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TempadItemsImpl {
    public static Supplier<Item> registerItem(String name, Supplier<Item> item) {
        var registry = Registry.register(Registry.ITEM, new ResourceLocation(Constants.MODID, name), item.get());
        return () -> registry;
    }
}
