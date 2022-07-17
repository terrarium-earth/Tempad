package me.codexadrian.tempad.forge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TempadLootModifier extends LootModifier {
    public static final Gson gson = new Gson();
    public final float odds;

    public TempadLootModifier(LootItemCondition[] args, float odds) {
        super(args);
        this.odds = odds;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> list, LootContext arg) {
        if (arg.getRandom().nextFloat() < this.odds) {
            list.add(ForgeTempad.CREATIVE_TEMPAD.get().getDefaultInstance());
        }
        return list;
    }

    public static class Serializer extends GlobalLootModifierSerializer<TempadLootModifier> {

        @Override
        public TempadLootModifier read(ResourceLocation arg, JsonObject jsonObject, LootItemCondition[] args) {
            float f = jsonObject.get("chance").getAsFloat();
            return new TempadLootModifier(args, f);
        }

        @Override
        public JsonObject write(TempadLootModifier lootModifier) {
            JsonObject jsonObject = this.makeConditions(lootModifier.conditions);
            jsonObject.add("chance", gson.toJsonTree(lootModifier.odds));
            return jsonObject;
        }
    }
}
