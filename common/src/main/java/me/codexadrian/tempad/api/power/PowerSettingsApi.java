package me.codexadrian.tempad.api.power;

import earth.terrarium.botarium.common.generic.base.ItemContainerLookup;
import me.codexadrian.tempad.api.ApiHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface PowerSettingsApi {
    PowerSettingsApi API = ApiHelper.load(PowerSettingsApi.class);

    @Nullable
    PowerSettings get(ItemStack stack);

    void registerItems(ItemContainerLookup.ItemGetter<PowerSettings, Void> getter, Supplier<Item>... containers);
}
