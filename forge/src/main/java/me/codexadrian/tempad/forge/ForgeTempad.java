package me.codexadrian.tempad.forge;

import com.mojang.serialization.Codec;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadType;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.items.TempadItem;
import me.codexadrian.tempad.network.NetworkHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static me.codexadrian.tempad.Constants.MODID;

@Mod(MODID)
public class ForgeTempad {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_TABLES = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final RegistryObject<EntityType<TimedoorEntity>> TIMEDOOR = ENTITIES.register("timedoor", () -> EntityType.Builder.of(TimedoorEntity::new, MobCategory.MISC).sized(.4F, 2.3F).build("timedoor"));
    public static final RegistryObject<TempadItem> TEMPAD = ITEMS.register("tempad", () -> new TempadItem(TempadType.NORMAL, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.EPIC)));
    public static final RegistryObject<TempadItem> CREATIVE_TEMPAD = ITEMS.register("he_who_remains_tempad", () -> new TempadItem(TempadType.HE_WHO_REMAINS, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.EPIC)));

    public static final RegistryObject<TempadLootModifier.Serializer> TEMPAD_LOOT_MODIFIER = LOOT_TABLES.register("tempad_loot_modifier", TempadLootModifier.Serializer::new);

    public ForgeTempad() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        LOOT_TABLES.register(bus);
        TempadImpl.SOUND_EVENTS.register(bus);
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(this);
        Tempad.init();
    }

    @SubscribeEvent
    public static void runData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(new LootTableDataProvider(gen, MODID));
    }

    private static class LootTableDataProvider extends GlobalLootModifierProvider {

        public LootTableDataProvider(DataGenerator gen, String modid) {
            super(gen, modid);
        }

        @Override
        protected void start() {
            add("tempad_loot_modifier", TEMPAD_LOOT_MODIFIER.get(), new TempadLootModifier());
        }
    }
}
