package me.codexadrian.tempad.common.forge;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

public class TempadLootModifier implements IGlobalLootModifier {
    public static final TempadLootModifier INSTANCE = new TempadLootModifier();

    @Override
    public @NotNull ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> objectArrayList, LootContext arg) {
        if (arg.getRandom().nextFloat() < 0.005F && arg.getQueriedLootTableId().equals(BuiltInLootTables.END_CITY_TREASURE)) {
            objectArrayList.add(TempadRegistry.CREATIVE_TEMPAD.get().getDefaultInstance());
        }
        return objectArrayList;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ForgeTempad.TEMPAD_LOOT_MODIFIER.get();
    }
}
