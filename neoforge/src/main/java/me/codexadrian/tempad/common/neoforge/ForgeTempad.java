package me.codexadrian.tempad.common.neoforge;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.compat.curios.TempadCurioHandler;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.s2c.InitConfigPacket;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.PlatformUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static me.codexadrian.tempad.common.Tempad.MODID;

@Mod(Tempad.MODID)
public class ForgeTempad {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_TABLES = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<TempadLootModifier>> TEMPAD_LOOT_MODIFIER = LOOT_TABLES.register("tempad_loot_modifier", TempadLootModifier.CODEC);

    public ForgeTempad(IEventBus bus) {
        Tempad.init();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        LOOT_TABLES.register(bus);
        TempadImpl.SOUND_EVENTS.register(bus);
        bus.addListener(this::setup);

        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == Tempad.TAB) TempadRegistry.ITEMS.stream().map(RegistryEntry::get).forEach(event::accept);
        });

        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            NetworkHandler.CHANNEL.sendToPlayer(new InitConfigPacket(TempadConfig.allowInterdimensionalTravel,
                TempadConfig.allowExporting,
                TempadConfig.tempadFuelType,
                TempadConfig.tempadFuelConsumptionValue,
                TempadConfig.tempadFuelCapacityValue,
                TempadConfig.advancedTempadFuelType,
                TempadConfig.advancedTempadfuelConsumptionValue,
                TempadConfig.advancedTempadfuelCapacityValue), event.getEntity());
        });
    }

    private void setup(final FMLCommonSetupEvent evt) {
        if (PlatformUtils.isModLoaded("curios")) {
            TempadCurioHandler.init();
        }
    }
}
