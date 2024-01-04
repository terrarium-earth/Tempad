package me.codexadrian.tempad.common.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BaubleUtils {

    @ExpectPlatform
    @Nullable
    public static Consumer<ItemStack> findTempadInBaubles(Player player, Consumer<ItemStack> setTempad) {
        throw new NotImplementedException("Baubles is not implemented yet!");
    }
}
