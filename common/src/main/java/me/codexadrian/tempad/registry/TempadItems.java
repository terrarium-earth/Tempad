package me.codexadrian.tempad.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.TempadType;
import me.codexadrian.tempad.items.TempadItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TempadItems {
    public static final Supplier<Item> TEMPAD = registerItem("tempad", () -> new TempadItem(TempadType.NORMAL, new Item.Properties()));
    public static final Supplier<Item> ADVANCED_TEMPAD = registerItem("he_who_remains_tempad", () -> new TempadItem(TempadType.HE_WHO_REMAINS, new Item.Properties()));

    @ExpectPlatform
    public static Supplier<Item> registerItem(String name, Supplier<Item> item) {
        throw new AssertionError();
    }

    public static void register() {

    }
}
