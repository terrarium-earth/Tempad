package me.codexadrian.tempad.forge;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TempadLootModifier extends LootModifier {

    protected TempadLootModifier() {
        super(new LootItemCondition[]{LootTableIdCondition.builder(BuiltInLootTables.END_CITY_TREASURE).build()});
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> list, LootContext arg) {
        if (arg.getRandom().nextFloat() < 0.005F) {
            list.add(ForgeTempad.CREATIVE_TEMPAD.get().getDefaultInstance());
        }
        return list;
    }

    public static class Serializer extends GlobalLootModifierSerializer<TempadLootModifier> {

        @Override
        public TempadLootModifier read(ResourceLocation arg, JsonObject jsonObject, LootItemCondition[] args) {
            return new TempadLootModifier();
        }

        @Override
        public JsonObject write(TempadLootModifier iGlobalLootModifier) {
            return this.makeConditions(iGlobalLootModifier.conditions);
        }
    }
}
