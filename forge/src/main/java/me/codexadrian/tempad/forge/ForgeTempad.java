package me.codexadrian.tempad.forge;

import com.mojang.serialization.Codec;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.network.NetworkHandler;
import me.codexadrian.tempad.registry.forge.TempadBlocksImpl;
import me.codexadrian.tempad.registry.forge.TempadEntitiesImpl;
import me.codexadrian.tempad.registry.forge.TempadItemsImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static me.codexadrian.tempad.Constants.MODID;

@Mod(MODID)
public class ForgeTempad {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_TABLES = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final RegistryObject<Codec<TempadLootModifier>> TEMPAD_LOOT_MODIFIER = LOOT_TABLES.register("tempad_loot_modifier", () -> Codec.unit(() -> TempadLootModifier.INSTANCE));

    public ForgeTempad() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Tempad.init();
        TempadItemsImpl.ITEMS.register(bus);
        TempadEntitiesImpl.ENTITY_TYPES.register(bus);
        TempadBlocksImpl.BLOCKS.register(bus);
        TempadBlocksImpl.BLOCK_ENTITY_TYPES.register(bus);
        LOOT_TABLES.register(bus);
        TempadImpl.SOUND_EVENTS.register(bus);
        NetworkHandler.register();
        bus.addListener(ForgeTempad::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ForgeTempadClient::registerBlurReloader);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        ForgeTempadClient.init();
    }
}
