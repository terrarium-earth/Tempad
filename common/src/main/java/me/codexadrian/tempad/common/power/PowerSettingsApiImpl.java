package me.codexadrian.tempad.common.power;

import earth.terrarium.botarium.common.generic.LookupApi;
import earth.terrarium.botarium.common.generic.base.ItemContainerLookup;
import me.codexadrian.tempad.api.power.PowerSettings;
import me.codexadrian.tempad.api.power.PowerSettingsApi;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PowerSettingsApiImpl implements PowerSettingsApi {
    private final ItemContainerLookup<PowerSettings, Void> items = LookupApi.createItemLookup(new ResourceLocation(Tempad.MODID, "power_attachments"), PowerSettings.class);

    @Override
    public @Nullable PowerSettings get(ItemStack stack) {
        return items.find(stack, null);
    }

    @Override
    public void registerItems(ItemContainerLookup.ItemGetter<PowerSettings, Void> getter, Supplier<Item>... containers) {
        items.registerItems(getter, containers);
    }
}
