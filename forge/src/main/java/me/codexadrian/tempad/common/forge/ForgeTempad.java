package me.codexadrian.tempad.common.forge;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static me.codexadrian.tempad.common.Tempad.MODID;

@Mod(Tempad.MODID)
public class ForgeTempad {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_TABLES = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<Codec<TempadLootModifier>> TEMPAD_LOOT_MODIFIER = LOOT_TABLES.register("tempad_loot_modifier", TempadLootModifier.CODEC);

    public ForgeTempad() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Tempad.init();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        LOOT_TABLES.register(bus);
        TempadImpl.SOUND_EVENTS.register(bus);
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ForgeTempadClient::registerBlurReloader);
        bus.addListener(this::setup);

        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTab() == BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.TOOLS_AND_UTILITIES)) TempadRegistry.ITEMS.stream().map(RegistryEntry::get).forEach(event::accept);
        });

        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            NetworkHandler.CHANNEL.sendToPlayer(new InitConfigPacket(TempadConfig.allowInterdimensionalTravel,
                TempadConfig.allowExporting,
                TempadConfig.consumeCooldown,
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
