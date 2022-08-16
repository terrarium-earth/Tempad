package me.codexadrian.tempad.registry.forge;

import me.codexadrian.tempad.Constants;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class TempadItemsImpl {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);

    public static Supplier<Item> registerItem(String name, Supplier<Item> item) {
        return ITEMS.register(name, item);
    }
}
