package me.codexadrian.tempad.fabric;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.mixin.loot.LootTableAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;

public class FabricTempad implements ModInitializer {
    @Override
    public void onInitialize() {
        Tempad.init();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Constants.MODID, "timedoor"), FabricTempadRegistry.TIMEDOOR_ENTITY_ENTITY_TYPE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MODID, "tempad"), FabricTempadRegistry.TEMPAD);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MODID, "he_who_remains_tempad"), FabricTempadRegistry.CREATIVE_TEMPAD);

        NetworkHandler.register();
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (id.equals(BuiltInLootTables.END_CITY_TREASURE)) {
                LootPool.Builder poolBuilder = new LootPool.Builder()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.005F))
                        .add(LootItem.lootTableItem(FabricTempadRegistry.CREATIVE_TEMPAD))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
