package me.codexadrian.tempad.common.forge;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TempadLootModifier extends LootModifier {
    public static final Supplier<Codec<TempadLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, TempadLootModifier::new)));
    protected TempadLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> objectArrayList, LootContext arg) {
        objectArrayList.add(TempadRegistry.CREATIVE_TEMPAD.get().getDefaultInstance());
        return objectArrayList;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ForgeTempad.TEMPAD_LOOT_MODIFIER.get();
    }
}
