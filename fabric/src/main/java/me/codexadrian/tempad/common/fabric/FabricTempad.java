package me.codexadrian.tempad.common.fabric;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.compat.fabricwaystones.FabricWaystoneLocationGetter;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.s2c.InitConfigPacket;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.PlatformUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class FabricTempad implements ModInitializer {
    @Override
    public void onInitialize() {
        Tempad.init();

        NetworkHandler.register();
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (id.equals(BuiltInLootTables.END_CITY_TREASURE)) {
                LootPool.Builder poolBuilder = new LootPool.Builder()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.005F))
                        .add(LootItem.lootTableItem(TempadRegistry.CREATIVE_TEMPAD.get()))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
                tableBuilder.pool(poolBuilder.build());
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            NetworkHandler.CHANNEL.sendToPlayer(new InitConfigPacket(TempadConfig.allowInterdimensionalTravel,
                TempadConfig.allowExporting,
                TempadConfig.consumeCooldown,
                TempadConfig.tempadFuelType,
                TempadConfig.tempadFuelConsumptionValue,
                TempadConfig.tempadFuelCapacityValue,
                TempadConfig.advancedTempadFuelType,
                TempadConfig.advancedTempadfuelConsumptionValue,
                TempadConfig.advancedTempadfuelCapacityValue), handler.player);
        });

        if (PlatformUtils.isModLoaded("fwaystones")) {
            FabricWaystoneLocationGetter.init();
        }

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(group -> {
            TempadRegistry.ITEMS.stream().map(RegistryEntry::get).forEach(group::accept);
        });
    }
}
